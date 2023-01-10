package com.potus.app.garden.service;


import com.potus.app.admin.model.BanRequest;
import com.potus.app.admin.repository.BanRequestRepository;
import com.potus.app.exception.*;
import com.potus.app.garden.model.*;
import com.potus.app.garden.repository.ChatMessageRepository;
import com.potus.app.garden.repository.GardenMemberRepository;
import com.potus.app.garden.repository.GardenRepository;
import com.potus.app.garden.repository.ReportRepository;
import com.potus.app.garden.utils.GardenExceptionMessages;
import com.potus.app.garden.utils.GardenUtils;
import com.potus.app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.potus.app.garden.utils.GardenExceptionMessages.*;

@Service
public class GardenService {

    @Autowired
    GardenRepository gardenRepository;

    @Autowired
    GardenMemberRepository gardenMemberRepository;

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    BanRequestRepository banRequestRepository;

    public List<Garden> getAll(){
        return gardenRepository.findAll();
    }

    public boolean existsByName(String name) {
        return gardenRepository.existsByName(name);
    }

    @Transactional
    public GardenMember createGarden(User user, String name) {

        Garden garden = new Garden(name);
        GardenMember gardenMember = new GardenMember(garden, user, GardenRole.OWNER);
        garden.setMembers(Collections.singleton(gardenMember));
        saveFullGarden(garden);
        return gardenMember;
    }

    private void saveFullGarden(Garden garden) {
        gardenMemberRepository.saveAll(garden.getMembers());
        gardenRepository.save(garden);
    }

    public Garden findByName(String name){
        return gardenRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException(GARDEN_DOES_NOT_EXISTS));
    }

    @Transactional
    public void removeUser(GardenMember member){
        if(member.getRole().equals(GardenRole.OWNER))
            throw  new ConflictException(GARDEN_OWNER_CAN_NOT_EXIT);

        Garden garden = member.getGarden();
        gardenMemberRepository.delete(member);
        garden.getMembers().remove(member);
        gardenRepository.save(garden);
    }

    public void deleteGarden(Garden garden) {
        gardenRepository.delete(garden);
    }

    public GardenMember findByUser(User user) {
        return gardenMemberRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException(USER_HAS_NOT_GARDEN));
    }

    public Garden editDescription(Garden garden, String description) {
        garden.setDescription(description);
        return gardenRepository.save(garden);
    }

    public GardenMember addUser(Garden garden, User user) {

        if(garden.getMembers().size() == GardenUtils.GARDEN_MAX_SIZE)
            throw new ConflictException(GARDEN_MAX_SIZE);

        if(user.getGarden() != null)
            throw new ConflictException(USER_HAS_GARDEN);

        GardenMember gardenMember = new GardenMember(garden, user, GardenRole.NORMAL);
        garden.getMembers().add(gardenMember);
        garden.setMembers(garden.getMembers());
        saveFullGarden(garden);
        return gardenMember;
    }

    @Transactional
    public void changeOwner(GardenMember member, GardenMember memberRequest) {
        if(! member.getRole().equals(GardenRole.OWNER))
            throw new BadRequestException(MEMBER_IS_NOT_OWNER);

        changeRole(member, GardenRole.ADMIN);
        changeRole(memberRequest, GardenRole.OWNER);
    }

    public void changeRole(GardenMember member, GardenRole role){
        member.setRole(role);
        gardenMemberRepository.save(member);
    }

    public List<GardenMember> getMembers(Garden garden) {
        return gardenMemberRepository.findByGarden(garden);
    }


    public ChatMessage saveChatMessage(ChatMessageDTO message, User user, String room){
        return chatMessageRepository.save(new ChatMessage(message.getId(),user, new Date(), room, message.getStatus()));
    }

    public List<ChatMessage> getAllChats() {
        return chatMessageRepository.findAll();
    }

    public List<ChatMessage> findMessagesByGarden(Garden garden, int page) {
        Pageable sortByDate = PageRequest.of(page, 20, Sort.by("date").descending());
        return chatMessageRepository.findByRoom(garden.getId().toString(), sortByDate);
    }

    public ChatMessage findMessageById(String message) {
        return chatMessageRepository.findById(message).orElseThrow(() -> new ResourceNotFoundException("MESSAGE NOT FOUND"));
    }

    @Transactional
    public Report reportUser(User reporter, ChatMessage chatMessage) {

        BanRequest banRequest = banRequestRepository.findByUser(chatMessage.getSender());


        if(banRequest == null){
            banRequest = new BanRequest(chatMessage.getSender());
            banRequest.setReports(new ArrayList<>());
        }
        else{
            List<Report> reports = banRequest.getReports();

            reports.forEach(report -> {
                Instant reportInstant = report.getDate().toInstant().plus(2, ChronoUnit.MINUTES);
                if(report.getReporter().equals(reporter) && reportInstant.isAfter(Instant.now())){
                    throw new TooManyRequestsException("YOU ALREADY REPORTED THAT ACCOUNT TRY LATER");
                }
            });
        }


        Report report = new Report(reporter, new Date(), chatMessage);

        reportRepository.save(report);
        List<Report> reports = banRequest.getReports();
        reports.add(report);
        banRequestRepository.save(banRequest);
        return report;
    }
}

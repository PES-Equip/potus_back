package com.potus.app.admin.service;

import com.potus.app.admin.model.APIToken;
import com.potus.app.admin.model.BanRequest;
import com.potus.app.admin.model.BannedAccount;
import com.potus.app.admin.repository.APITokenRepository;
import com.potus.app.admin.repository.BanRequestRepository;
import com.potus.app.admin.repository.BannedAccountRepository;
import com.potus.app.exception.ResourceAlreadyExistsException;
import com.potus.app.exception.ResourceNotFoundException;
import com.potus.app.garden.model.ChatMessage;
import com.potus.app.garden.repository.ChatMessageRepository;
import com.potus.app.garden.repository.ReportRepository;
import com.potus.app.user.model.User;
import com.potus.app.user.repository.UserRepository;
import com.potus.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

import static com.potus.app.admin.utils.AdminExceptionMessages.*;
import static com.potus.app.admin.utils.AdminUtils.ABC;
import static com.potus.app.admin.utils.AdminUtils.APITOKEN_LEN;

@Service
public class AdminService {

    @Autowired
    APITokenRepository apiTokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BannedAccountRepository bannedAccountRepository;

    @Autowired
    BanRequestRepository banRequestRepository;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    ChatMessageRepository chatMessageRepository;

    private static SecureRandom random = new SecureRandom();


    public boolean existsToken(String token){
        return apiTokenRepository.existsByToken(token);
    }


    private String generateToken(){
        StringBuilder sb = new StringBuilder(APITOKEN_LEN);
        for(int i = 0; i < APITOKEN_LEN; i++)
            sb.append(ABC.charAt(random.nextInt(ABC.length())));

        return sb.toString();
    }

    public boolean existsByName(String name){
        return apiTokenRepository.existsByName(name);
    }

    public APIToken createToken(String name){

        if(existsByName(name))
            throw new ResourceAlreadyExistsException(TOKEN_NAME_ALREADY_EXISTS);

        String newTokenValue = generateToken();

        while(existsToken(newTokenValue))
            newTokenValue = generateToken();

        APIToken token = new APIToken(newTokenValue, name);

        return apiTokenRepository.save(token);
    }

    public void deleteToken(APIToken token) {
        apiTokenRepository.delete(token);
    }

    public List<APIToken> getAllTokens() {
        return apiTokenRepository.findAll();
    }

    public APIToken findByName(String name) {
        return apiTokenRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException(TOKEN_DOES_NOT_EXISTS));
    }

    public User addAdmin(User user) {

        if (user.getAdmin()) {
            throw new ResourceAlreadyExistsException(USER_IS_ALREADY_ADMIN);
        }

        user.setAdmin(true);
        return userRepository.save(user);
    }

    public User deleteAdmin(User user) {

        if (!user.getAdmin()) {
            throw new ResourceAlreadyExistsException(USER_IS_NOT_AN_ADMIN);
        }

        user.setAdmin(false);
        return userRepository.save(user);
    }

    public APIToken findByToken(Long id) {
        return apiTokenRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(TOKEN_DOES_NOT_EXISTS));
    }

    public APIToken refreshToken(APIToken token) {
        String newTokenValue = generateToken();

        while(existsToken(newTokenValue))
            newTokenValue = generateToken();

        token.setToken(newTokenValue);
        return apiTokenRepository.save(token);
    }

    public APIToken renameToken(APIToken token, String name) {

        if(existsByName(name))
            throw new ResourceAlreadyExistsException(TOKEN_NAME_ALREADY_EXISTS);

        token.setName(name);
        return apiTokenRepository.save(token);
    }

    public boolean emailIsBanned(String email) {
        return bannedAccountRepository.existsByEmail(email);
    }

    public BannedAccount findByEmail(String email){
        return bannedAccountRepository.findByEmail(email);
    }

    @Transactional
    public BannedAccount banAccount(User user, String reason) {

        if(emailIsBanned(user.getEmail()))
            throw new ResourceAlreadyExistsException("EMAIL IS ALREADY BANNED");

        deleteBanRequest(user);
        BannedAccount bannedAccount = new BannedAccount(user.getEmail(), reason, new Date());
        return bannedAccountRepository.save(bannedAccount);
    }

    @Transactional
    public void deleteBanRequest(User user){
        BanRequest banRequest = banRequestRepository.findByUser(user);

        if(banRequest != null){
            reportRepository.deleteAll(banRequest.getReports());
            banRequestRepository.delete(banRequest);
        }
    }

    public List<BannedAccount> findAllBannedAccounts(){
        return bannedAccountRepository.findAll();
    }

    public List<BanRequest> getBanRequests() {
        return banRequestRepository.findAll();
    }

    public ChatMessage findChatMessageById(String chatId) {
        return  chatMessageRepository.findById(chatId).orElseThrow(() -> new ResourceNotFoundException("CHAT MESSAGE NOT FOUND"));
    }

    public List<ChatMessage> getPreviousMessages(ChatMessage chatMessage, int page) {
        Pageable sortByDate = PageRequest.of(page, 20, Sort.by("date").ascending());
        return chatMessageRepository.findByDateLessThanEqualAndRoom(chatMessage.getDate(), chatMessage.getRoom(), sortByDate);
    }
}

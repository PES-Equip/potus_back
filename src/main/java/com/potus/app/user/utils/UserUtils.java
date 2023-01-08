package com.potus.app.user.utils;

import com.potus.app.exception.BadRequestException;
import com.potus.app.potus.model.Actions;
import com.potus.app.user.model.TrophyType;
import com.potus.app.user.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.potus.app.potus.utils.PotusExceptionMessages.ACTION_DOES_NOT_EXISTS;

public class UserUtils {

    private UserUtils(){}

    public static User getUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Actions getActionFormatted(String action) throws BadRequestException {
        try {
            return Actions.valueOf(action.toUpperCase());
        } catch (Exception e){
            throw new BadRequestException(ACTION_DOES_NOT_EXISTS);
        }
    }

    public static TrophyType getTrophyTypeFormatted(String type) throws BadRequestException {
        try {
            return TrophyType.valueOf(type.toUpperCase());
        } catch (Exception e){
            throw new BadRequestException(ACTION_DOES_NOT_EXISTS);
        }
    }

    public static List<String> adminUsers() {
        List<String> adminMails = new ArrayList<>();
        adminMails.add("cristian.mesa.sanchez@estudiantat.upc.edu");
        adminMails.add("alex.moa@estudiantat.upc.edu");
        adminMails.add("artur.farriols@estudiantat.upc.edu");

        return adminMails;
    }

    public static int monthsBetween(Date d1, Date d2){
        if(d2==null || d1==null){
            return -1;//Error
        }
        Calendar m_calendar=Calendar.getInstance();
        m_calendar.setTime(d1);
        int nMonth1=12*m_calendar.get(Calendar.YEAR)+m_calendar.get(Calendar.MONTH);
        m_calendar.setTime(d2);
        int nMonth2=12*m_calendar.get(Calendar.YEAR)+m_calendar.get(Calendar.MONTH);
        return java.lang.Math.abs(nMonth2-nMonth1);
    }

   public static int calculateTrophyNextLevel(int base, int level){
       return (int) (base * Math.pow(2,level));
   }
}

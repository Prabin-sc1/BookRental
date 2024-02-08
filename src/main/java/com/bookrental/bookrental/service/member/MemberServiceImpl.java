package com.bookrental.bookrental.service.member;

import com.bookrental.bookrental.config.CustomMessageSource;
import com.bookrental.bookrental.constants.ModuleNameConstants;
import com.bookrental.bookrental.enums.Message;
import com.bookrental.bookrental.exception.AppException;
import com.bookrental.bookrental.helpers.Helper;
import com.bookrental.bookrental.mapper.MemberMapper;
import com.bookrental.bookrental.model.Author;
import com.bookrental.bookrental.model.Member;
import com.bookrental.bookrental.pojo.member.MemberRequestPojo;
import com.bookrental.bookrental.pojo.member.MemberResponsePojo;
import com.bookrental.bookrental.repository.MemberRepository;
import com.bookrental.bookrental.utils.NullAwareBeanUtilsBean;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final NullAwareBeanUtilsBean beanUtils = new NullAwareBeanUtilsBean();
    private final MemberMapper memberMapper;
    private final CustomMessageSource customMessageSource;

    @Override
    public void createMember(MemberRequestPojo memberRequestPojo) {
        Member member = new Member();
        if (memberRequestPojo.getId() != null) {
            member = memberRepository.findById(memberRequestPojo.getId()).orElse(member);
        }
        try {
            beanUtils.copyProperties(member, memberRequestPojo);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AppException(e.getMessage());
        }
        member.setEmail(memberRequestPojo.getEmail().toLowerCase());
        try {
            memberRepository.save(member);
        } catch (Exception e) {
            throw new AppException(customMessageSource.get(Message.ALREADY_EXISTS.getCode(), ModuleNameConstants.MEMBER));
        }
    }

    @Override
    public MemberResponsePojo getMemberById(Integer id) {
        return memberMapper.getSingleMember(id).orElseThrow(() ->
                new AppException(customMessageSource.get(Message.ID_NOT_FOUND.getCode(), ModuleNameConstants.MEMBER)));
    }

    @Override
    public List<MemberResponsePojo> getAllMember() {
        return memberMapper.getAllMember();
    }

    @Override
    public void deleteMember(Integer id) {
        memberRepository.deleteById(id);
    }

    @Override
    public Member findMemberById(Integer id) {
        return memberRepository.findById(id).orElseThrow(() ->
                new AppException(customMessageSource.get(Message.ID_NOT_FOUND.getCode(), ModuleNameConstants.MEMBER)));
    }

    @Override
    public Member findMemberByName(String name) {
        return memberRepository.findByName(name).orElseThrow(() ->
                new AppException(customMessageSource.get(Message.ID_NOT_FOUND.getCode(), ModuleNameConstants.MEMBER)));
    }


    public static String SHEET_NAME = "author";

    public static String[] getHeaders(Class<?> className) {
        List<String> headers = new ArrayList<>();
        Field[] fields = className.getDeclaredFields();
        for (Field field : fields) {
            headers.add(field.getName());
        }
        return headers.toArray(new String[headers.size()]);
    }

    public ByteArrayInputStream getExcelData() throws IOException {
        List<Member> all = memberRepository.findAll();
        ByteArrayInputStream byteArrayInputStream = Helper.dataToExcel(all, SHEET_NAME, getHeaders(Member.class));
        return byteArrayInputStream;
    }


    public void save(MultipartFile file) {
        try {
            List<Member> list = Helper.convertExcelToList(Member.class, file.getInputStream());
            memberRepository.saveAll(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

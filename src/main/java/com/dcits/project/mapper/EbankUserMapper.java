package com.dcits.project.mapper;

import com.dcits.project.pojo.EbankUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface EbankUserMapper {
    int deleteByPrimaryKey(String ebankUserId);

    int insert(EbankUser record);

    EbankUser selectByPrimaryKey(String ebankUserId);

    List<EbankUser> selectAll();

    int updateByPrimaryKey(EbankUser record);
}
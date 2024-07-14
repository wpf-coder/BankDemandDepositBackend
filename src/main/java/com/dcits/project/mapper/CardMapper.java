package com.dcits.project.mapper;

import com.dcits.project.pojo.Card;
import com.dcits.project.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface CardMapper {
    int deleteByPrimaryKey(String cardId);

    int insert(Card record);

    Card selectByPrimaryKey(String cardId);

    List<Card> selectAll();

    /**
     * 根据银行卡号更新银行卡信息。注意只能更新 密码、状态、余额。
     * @how:
     * @return:
     *       int
    **/
    int updateByPrimaryKey(Card record);

    Card selectByFeature(Card feature);

    List<Card> selectByUserInformation(@Param("user") User user);

    /**
     * 获得所有的银行卡，该银行卡信息联代累计计息数信息。
     * @how:
     * @return:
     *       java.util.List<com.dcits.project.pojo.Card>
    **/
    List<Card> selectAllCardLinkDemandInterest();

}
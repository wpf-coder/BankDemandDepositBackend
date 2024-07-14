package com.dcits.project.mapper;

import com.dcits.project.pojo.Branch;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BranchMapper {
    int deleteByPrimaryKey(String branchId);

    int insert(Branch record);

    Branch selectByPrimaryKey(String branchId);

    List<Branch> selectAll();

    int updateByPrimaryKey(Branch record);
}
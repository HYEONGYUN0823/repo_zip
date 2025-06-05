package com.a7a7.modeule.codegroup;

import java.util.List;
import org.apache.ibatis.annotations.Param; // @Param 어노테이션 import 추가
import org.springframework.stereotype.Repository;

@Repository
public interface CodeGroupDao {

    public List<CodeGroupDto> selectList(CodeGroupVo vo);

    public CodeGroupDto selectOne(CodeGroupVo vo);

    public List<CodeGroupDto> selectListWithoutPaging();

    public int insert(CodeGroupDto codeGroupDto);

    public int update(CodeGroupDto codeGroupDto);

    public int selectOneCount(CodeGroupVo vo);

    public int uelete(CodeGroupDto codeGroupDto);

    /**
     * 그룹 코드와 상세 코드로 코드 이름을 조회합니다.
     * @param ifcgSeq 그룹 코드 ID (예: "3" for 브랜드, String 타입)
     * @param ifcdSeq 상세 코드 ID (ProductDto의 brandName 값, 문자열로 변환된 형태)
     * @return 코드 이름 (String), 없으면 null
     */
    public String selectCodeNameByCodes(@Param("ifcgSeq") String ifcgSeq, @Param("ifcdSeq") String ifcdSeq);
}
package com.a7a7.modeule.member;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/member")
public class MemberRestController extends MemberController{
	
	@Autowired
	MemberService service;
	
	@GetMapping("")
	public List<MemberDto> selectList(MemberVo vo) throws Exception {
		List<MemberDto> list = service.selectList(vo);
		return list;
	}
	
	@GetMapping("/{seq}")
	public MemberDto selectOne(@PathVariable("seq") String seq) throws Exception {
	    MemberDto dto = new MemberDto();
	    dto.setSeq(seq);
	    return service.selectOne(dto);
	}
	
	@PatchMapping("/{seq}")
	public void update(@PathVariable String seq, MemberDto dto) throws Exception {
		
		
		System.out.println("seq: " + seq);
		System.out.println("dto.getiD(): " + dto.getiD());
		System.out.println("dto.getName(): " + dto.getName());
		
		dto.setSeq(seq);
		service.update(dto);
	}
	
}

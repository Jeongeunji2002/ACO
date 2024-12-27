package org.myweb.first.member.model.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myweb.first.member.jpa.entity.MemberEntity;
import org.myweb.first.member.jpa.repository.MemberRepository;
import org.myweb.first.member.model.dto.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j    //Logger 객체 선언임, 별도의 로그객체 선언 필요없음, 제공되는 레퍼런스는 log 임
@Service
@RequiredArgsConstructor	//매개변수 있는 생성자를 반드시 실행시켜야 한다는 설정임
@Transactional
public class MemberService {
	//jpa 가 제공하는 기본 메소드와 추가한 메소드 사용
	private final MemberRepository memberRepository;	
	//QueryDSL 사용 첫번째
	//4. Service 에서는 MemberRepository 만 사용하면 됨

	@Transactional
	public int insertMember(Member member) {
		//save() -> 성공시 Entity, 실패시 null 리턴함, JPA 가 제공하는 메소드임
		try{
			memberRepository.save(member.toEntity()).toDto();
			return 1;
		}catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return 0;
		}
	}

	public Member selectMember(String userId) {
		//jpa 제공 메소드 사용
		//findById(id) : Optional<T>
		//엔티티에 등록된 id 를 사용해서 entity 조회함
		Optional<MemberEntity> entityOptional = memberRepository.findById(userId);
		return entityOptional.get().toDto();
	}

	@Transactional
	public int updateMember(Member member) {
		//save() -> 성공시 Entity, 실패시 null 리턴함, JPA 가 제공하는 메소드임
		try{
			memberRepository.save(member.toEntity()).toDto();
			return 1;
		}catch (Exception e) {
			log.error(e.getMessage());
			return 0;
		}
	}

	@Transactional
	public int deleteMember(String userId) {
		try {   //리턴 타입을 int 로 맞추기 위해서 처리함
			//deleteById() -> 리턴 타입이 void 임
			//전달인자인 userid 가 null 인 경우 IllegalArgumentException 발생함
			memberRepository.deleteById(userId);
			return 1;
		} catch (Exception e) {
			log.info("deleteMember : " + e.getMessage());
			return 0;
		}
	}

	//회원가입시 아이디 중복 검사용
	public int selectCheckId(String userid) {
		return memberRepository.existsById(userid) ? 1 : 0;	//jpa가 제공
		//userid 가 존재하면 true, 없으면 false 리턴함
	}

	//관리자용 ******************************************
	public int selectListCount() {
		return (int)memberRepository.count();  //jpa 제공
	}

	public ArrayList<Member> selectList(Pageable pageable) {
		Page<MemberEntity> entityList = memberRepository.findAll(pageable);  //jpa 제공
		ArrayList<Member> list = new ArrayList<>();
		//Page 안의 MemberEntity 를 Member 로 변환해서 리스트에 추가 처리함
		for(MemberEntity entity : entityList){
			//전체 조회이므로 관리자가 아닌 회원만 리스트에 저장함
			if(entity.getAdminYN().equals("N")) {
				list.add(entity.toDto());
			}
		}
		return list;
	}

	/*public Page<Member> selectList(Pageable pageable) {
		Page<MemberEntity> entityList = memberRepository.findAll(pageable);  //jpa 제공
		return entityList.map(MemberEntity::toDto); // MemberEntity의 toDto() 메서드를 사용
	}*/

	public int updateLoginOK(String userId, String loginOk) {
		try {
			//이전 데이터를 가진 회원정보를 조회해 옴 (수정전)
			Member updateMember = memberRepository.findById(userId).get().toDto();
			//전달받은 객체에서 loginOk 정보만 수정할 것이므로, 수정할 값으로 변경함
			updateMember.setLoginOk(loginOk);
			//수정할 객체를 가진 회원정보를 jpa 로 넘김
			memberRepository.save(updateMember.toEntity());  //jpa 제공
			return 1;
		}catch (Exception e) {
			log.error(e.getMessage());
			return 0;
		}
	}

	//검색 카운트 관련 ------------------------------------------------------------------
	public int selectSearchUserIdCount(String keyword) {
		// 추가 작성해서 사용 : 리포지토리 인터페이스에 추가 작성하
		return (int) memberRepository.countSearchUserId(keyword);
	}


	public int selectSearchGenderCount(String keyword) {
		// 추가 작성해서 사용 : 리포지토리 인터페이스에 추가 작성하
		return (int) memberRepository.countSearchGender(keyword);
	}


	public int selectSearchAgeCount(int age) {
		// 추가 작성해서 사용 : 리포지토리 인터페이스에 추가 작성하
		return (int) memberRepository.countSearchAge(age);
	}


	public int selectSearchEnrollDateCount(Date begin, Date end) {
		// 추가 작성해서 사용 : 리포지토리 인터페이스에 추가 작성하
		return (int) memberRepository.countSearchDate(begin, end);
	}


	public int selectSearchLoginOKCount(String keyword) {
		// 추가 작성해서 사용 : 리포지토리 인터페이스에 추가 작성하
		return (int) memberRepository.countSearchLoginOK(keyword);
	}

	//검색 관련 목록 조회용 --------------------------------------------------------------------

	public ArrayList<Member> selectSearchUserId(String keyword, Pageable pageable) {
		//추가해서 사용함 :
		List<MemberEntity> page = memberRepository.findSearchUserId(keyword, pageable);
		ArrayList<Member> list = new ArrayList<>();
		for(MemberEntity entity : page){
			list.add(entity.toDto());
		}
		return list;
	}


	public ArrayList<Member> selectSearchGender(String keyword, Pageable pageable) {
		//추가해서 사용함 :
		List<MemberEntity> page = memberRepository.findSearchGender(keyword, pageable);
		ArrayList<Member> list = new ArrayList<>();
		for(MemberEntity entity : page){
			list.add(entity.toDto());
		}
		return list;
	}


	public ArrayList<Member> selectSearchAge(int age, Pageable pageable) {
		//추가해서 사용함 :
		List<MemberEntity> page = memberRepository.findSearchAge(age, pageable);
		ArrayList<Member> list = new ArrayList<>();
		for(MemberEntity entity : page){
			list.add(entity.toDto());
		}
		return list;
	}


	public ArrayList<Member> selectSearchEnrollDate(Date begin, Date end, Pageable pageable) {
		//추가해서 사용함 :
		List<MemberEntity> page = memberRepository.findSearchDate(begin, end, pageable);
		ArrayList<Member> list = new ArrayList<>();
		for(MemberEntity entity : page){
			list.add(entity.toDto());
		}
		return list;
	}


	public ArrayList<Member> selectSearchLoginOK(String keyword, Pageable pageable) {
		//추가해서 사용함
		List<MemberEntity> page = memberRepository.findSearchLoginOK(keyword, pageable);
		ArrayList<Member> list = new ArrayList<>();
		for(MemberEntity entity : page){
			list.add(entity.toDto());
		}
		return list;
	}

}

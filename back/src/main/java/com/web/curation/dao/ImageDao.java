package com.web.curation.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.web.curation.model.Board;
import com.web.curation.model.ImageStore;


public interface ImageDao extends JpaRepository<ImageStore, String>{
	
	@Query(value="select i.imageurl, i.articleno, i.imageno " + 
			"from board b, imagestore i " + 
			"where b.articleuser = :nickname and b.articleno = i.articleno " + 
			"group by i.articleno "
			+ "order by i.articleno desc", nativeQuery = true)
	List<ImageStore> myBoardList(String nickname);
	
	@Query(value="select i.imageUrl , i.articleno, i.imageno " + 
			" from board b, bookmark m, imagestore i " + 
			" where b.articleno=m.bookedarticle and m.bookuser=:nickname and b.articleno=i.articleno " + 
			" group by i.articleno "
			+" order by i.articleno desc", nativeQuery=true)
	List<ImageStore> bookMarkImgList(@Param("nickname") String nickname);
	
	List<ImageStore> findImagestoreByArticleNoOrderByArticleNoDesc(int num);
	
	@Query(value="select imageno, imageUrl, articleNo from imagestore group by articleno order by articleno desc ",
			countQuery="select count(a.articleno) from (select articleno from imagestore group by articleno) a ",
			nativeQuery=true)
	Page<ImageStore> getAllSearchList(Pageable pageable);
	

}

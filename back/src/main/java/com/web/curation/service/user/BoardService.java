package com.web.curation.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.web.curation.dao.BoardTwoDao;
import com.web.curation.dao.ImageDao;
import com.web.curation.dao.RecellDao;
import com.web.curation.model.BoardDTO;
import com.web.curation.model.ImageStore;
import com.web.curation.model.Recell;
@Service
public class BoardService {
	
	@Autowired
	private BoardTwoDao boardTwoDao;
	
	@Autowired
	private ImageDao imageDao;
	
	@Autowired
	private RecellDao recellDao;
	
	public List<BoardDTO> getMainFeedList(int page, String nickname){
		PageRequest pageRequest = PageRequest.of(page, 10);
		
		return boardTwoDao.getMainFeedList(pageRequest, nickname).getContent();
		
	}
	
	public List<Recell> getRecellList(int page){
		PageRequest pageRequest = PageRequest.of(page,30);
		return recellDao.getRecellList();
	}
	
	
	
	public List<ImageStore> getAllSearchList(int page){
		PageRequest pageRequest = PageRequest.of(page, 60);
		return imageDao.getAllSearchList(pageRequest).getContent();
	}
}

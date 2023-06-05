package fp.art.stroke.board.model.dao;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import fp.art.stroke.board.model.vo.Board;
import fp.art.stroke.board.model.vo.BoardDetail;
import fp.art.stroke.board.model.vo.BoardType;
import fp.art.stroke.board.model.vo.Pagination;
import fp.art.stroke.member.controller.MemberController;


@Repository
public class BoardDAO {
	
	private Logger logger = LoggerFactory.getLogger(BoardDAO.class);
	@Autowired
	private SqlSessionTemplate sqlSession;
	
	
	public List<BoardType> selectBoardType() {
		// TODO Auto-generated method stub
		return sqlSession.selectList("boardMapper.selectBoardType");
	}


	public List<Board> selectBoardList(Pagination pagination, int boardCode) {
		int offset = ( pagination.getCurrentPage()-1) * pagination.getLimit();
		RowBounds rowBounds = new RowBounds(offset, pagination.getLimit());
		return sqlSession.selectList("boardMapper.selectBoardList",boardCode,rowBounds);
	}


	public int getListCount(int boardCode) {
		// TODO Auto-generated method stub
		return sqlSession.selectOne("boardMapper.getListCount",boardCode);
	}


	public BoardDetail selectBoardDetail(int boardId) {
		// TODO Auto-generated method stub
		return sqlSession.selectOne("boardMapper.selectBoardDetail",boardId);
	}

}

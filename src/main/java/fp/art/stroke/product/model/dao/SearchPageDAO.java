package fp.art.stroke.product.model.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository 
public class SearchPageDAO {

	@Autowired
	private SqlSessionTemplate sqlSession;
	
}

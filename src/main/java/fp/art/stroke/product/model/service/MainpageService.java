package fp.art.stroke.product.model.service;

import java.util.List;

import fp.art.stroke.product.model.vo.Product;
import fp.art.stroke.product.model.vo.WishList;

public interface MainpageService {

	/** 베스트 상품 불러오기 
	 * @param productName
	 * @return
	 */
	List<Product> selectMainBestProdcut(String productName);

	
	
	/** 키매, 하이퍼펜션 상품 불러오기 
	 * @return
	 */
	List<Product> selectMainArtistProdcut();


	
	/** 로그인한 회원의 위시리스트에 있는 productId 얻어오기
	 * @return
	 */
	List<WishList> selectWishProductId(int memberId);

}

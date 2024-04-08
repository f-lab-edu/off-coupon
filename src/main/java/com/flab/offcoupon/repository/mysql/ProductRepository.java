package com.flab.offcoupon.repository.mysql;

import com.flab.offcoupon.domain.entity.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface ProductRepository {
    /**
     * 상품 ID로 상품을 조회합니다.<br>
     * Product 객체를 찾을 수도 있고, 없을 수도 있습니다.
     * Optional을 return하여 선택권을 제공합니다.
     * @param productId
     * @return Optinal한 상품 객체
     */
    Optional<Product> findProductById(long productId);

    /**
     * 상품 ID로 상품을 조회해서 Product객체를 반환합니다.<br>
     * @param productId 상품 ID
     * @return 상품 객체
     */

    default Product getProductById(long productId) {
        return findProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 존재하지 않습니다. id: " + productId));
    }
}

package com.jack.service;

import com.jack.entity.Product;
import com.jack.entity.ProductRobbingRecord;
import com.jack.mapper.ProductMapper;
import com.jack.mapper.ProductRobbingRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author cwl
 * @description: TODO
 * @date 2020/8/26 7:31
 */
@Service
public class ConcurrencyService {
    private static final Logger log = LoggerFactory.getLogger(ConcurrencyService.class);
    private static final String ProductNo = "product_10010";

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductRobbingRecordMapper productRobbingRecordMapper;

    public void manageRobbing(String mobile) {
        //--v1.0
        // try {
        //     Product product = productMapper.selectByProductNo(ProductNo);
        //     if (product != null && product.getTotal() > 0) {
        //         log.info("当前手机号：{} 恭喜您抢到单了!", mobile);
        //         productMapper.updateTotal(product);
        //     } else {
        //         log.error("当前手机号：{} 抢不到单!", mobile);
        //     }
        // } catch (Exception e) {
        //     log.error("处理抢单发生异常：mobile={} ", mobile);
        // }

        //+v2.0
        try {
            Product product=productMapper.selectByProductNo(ProductNo);
            if (product!=null && product.getTotal()>0){
                int result=productMapper.updateTotal(product);
                if (result>0) {
                    ProductRobbingRecord entity=new ProductRobbingRecord();
                    entity.setMobile(mobile);
                    entity.setProductId(product.getId());
                    productRobbingRecordMapper.insertSelective(entity);
                    log.info("客户:{},抢单成功...", mobile);
                }
            }
        }catch (Exception e){
            log.error("处理抢单发生异常：mobile={} ",mobile);
        }
    }
}

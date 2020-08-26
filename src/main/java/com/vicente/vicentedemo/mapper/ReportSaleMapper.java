package com.vicente.vicentedemo.mapper;

import com.vicente.vicentedemo.entity.ReportSale;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 */
public interface ReportSaleMapper extends BaseMapper<ReportSale> {

    List<ReportSale> getSaleData(Map<String, Object> param);

}

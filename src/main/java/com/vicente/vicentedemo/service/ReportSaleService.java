package com.vicente.vicentedemo.service;

import cn.hutool.json.JSONObject;
import com.vicente.vicentedemo.entity.ReportSale;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 */
public interface ReportSaleService extends IService<ReportSale> {

    List<ReportSale> getSaleData(JSONObject param);

}

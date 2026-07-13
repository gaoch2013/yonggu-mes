package com.yonyou.dataswitch;

/**
 * @description: URL地址
 **/
public class OpenApiURL {

    /**
     * 计量单位列表查询
     */
    public final static String UNIT_LIST= "/yonbip/digitalModel/unit/list";
    /**
     * 计量单位详情查询
     */
    public final static String UNIT_DETAIL= "/yonbip/digitalModel/unit/detail";
    /**
     * 计量单位保存
     */
    public final static String UNIT_SAVE= "/yonbip/digitalModel/unit/save";

    /**
     * 组织单元列表查询
     */
    public final static String ORG_LIST= "/yonbip/digitalModel/orgunit/querytree";
    /**
     * 组织单元详情查询
     */
    public final static String ORG_DETAIL= "/yonbip/digitalModel/orgunit/detail";
    /**
     * 组织单元保存
     */
    public final static String ORG_SAVE= "/yonbip/digitalModel/orgunit/save";

    /**
     * 物料档案分页查询
     */
    public final static String PRODUCT_LIST= "/yonbip/digitalModel/product/listproductbycondition";
    /**
     * 物料档案批量详情查询
     */
    public final static String PRODUCT_DETAIL= "/yonbip/digitalModel/product/batchdetailnew";

    /**
     * 物料保存
     */
    public final static String PRODUCT_SAVE= "/yonbip/digitalModel/product/integration/batchsave";

    /**
     * 标准物料清单列表查询
     */
    public final static String BOM_LIST= "/yonbip/mfg/v1.0/bom/list";
    /**
     * 物料清单详情查询
     */
    public final static String BOM_DETAIL= "/yonbip/mfg/bom/detail";
    /**
     * 物料清单保存
     */
    public final static String BOM_SAVE= "/yonbip/mfg/bom/save/V1";

    /**
     * 工艺路线列表查询
     */
    public final static String ROUTING_LIST= "/yonbip/mfg/routing/list";
    /**
     * 工艺路线详情查询
     */
    public final static String ROUTING_DETAIL= "/yonbip/mfg/routing/detail";
    /**
     * 工艺路线保存
     */
    public final static String ROUTING_SAVE= "/yonbip/mfg/routing/save";

    /**
     * 现存量查询
     */
    public final static String STOCK_QUERY= "/yonbip/scm/stock/QueryCurrentStocksByCondition";

    /**
     * 现存量保存
     */
    public final static String STOCK_SAVE= "/yonbip/scm/stockanalysis/forcesave";

    /**
     * 货位现存量列表查询
     */
    public final static String STOCK_LOCATION_QUERY= "/yonbip/SCC/stock/QueryCurrentLocationStocksByCondition";
    /**
     * 货位现存量保存
     */
    public final static String STOCK_LOCATION_SAVE= "/yonbip/scm/stocklocation/forcesave";

    /**
     * 批次号批量查询
     */
    public final static String BATCHNO_LIST_QUERY= "/yonbip/scm/batchno/report/listbatch";

    /**
     * 材料出库单列表查询
     */
    public final static String MATERIAL_OUT_QUERY= "/yonbip/scm/materialout/list";
    /**
     * 材料出库单详情查询
     */
    public final static String MATERIAL_OUT_DETAIL= "/yonbip/scm/materialout/detail";
    /**
     * 材料出库单单个保存
     */
    public final static String MATERIAL_OUT_SAVE= "/yonbip/SCC/materialout/single/save";

    /**
     * 产成品入库单列表查询
     */
    public final static String STORE_PRO_RECORD_QUERY= "/yonbip/scm/storeprorecord/list";
    /**
     * 产成品入库单详情查询
     */
    public final static String STORE_PRO_RECORD_DETAIL= "/yonbip/scm/storeprorecord/detail";
    /**
     * 产成品入库单保存
     */
    public final static String STORE_PRO_RECORD_SAVE= "/yonbip/SCC/storeprorecord/single/save";

    /**
     * 其他出库单列表查询
     */
    public final static String OTHER_OUT_RECORD_QUERY= "/yonbip/scm/othoutrecord/list";
    /**
     * 其他出库单详情查询
     */
    public final static String OTHER_OUT_RECORD_DETAIL= "/yonbip/scm/othoutrecord/detail";
    /**
     * 其他出库单保存
     */
    public final static String OTHER_OUT_RECORD_SAVE= "/yonbip/scm/othoutrecord/single/save";

    /**
     * 领料申请单列表查询
     */
    public final static String PICKING_REQUISITION_QUERY= "/yonbip/scm/pickingrequisition/list";
    /**
     * 领料申请单详情查询
     */
    public final static String PICKING_REQUISITION_DETAIL= "/yonbip/scm/pickingrequisition/detail";
    /**
     * 领料申请单保存
     */
    public final static String PICKING_REQUISITION_SAVE= "/yonbip/scm/pickingrequisition/single/save";

    /**
     * 生产订单列表查询
     */
    public final static String PRODUCTION_ORDER_QUERY= "/yonbip/mfg/productionorder/list";
    /**
     * 生产订单详情查询
     */
    public final static String PRODUCTION_ORDER_DETAIL= "/yonbip/mfg/productionorder/detail";
    /**
     * 生产订单完工
     */
    public final static String PRODUCTION_ORDER_FINISH_WORK= "/yonbip/mfg/productionorder/batchfinishWork";

    /**
     * 完工报告单列表查询
     */
    public final static String FINISHED_REPORT_QUERY= "/yonbip/mfg/finishedreport/list";
    /**
     * 完工报告单详情查询
     */
    public final static String FINISHED_REPORT_DETAIL= "/yonbip/mfg/finishedreport/detail";
    /**
     * 完工报告单保存
     */
    public final static String FINISHED_REPORT_SAVE= "/yonbip/mfg/finishedreport/new/save";
    /**
     * 完工报告单审核
     */
    public final static String FINISHED_REPORT_AUDIT= "/yonbip/mfg/finishedreport/audit";
}

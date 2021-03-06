package cn.itcast.service;
import java.util.List;
import cn.itcast.pojo.TbGoods;

import cn.itcast.pojo.TbItem;
import entity.PageResult;
import pojogroup.Goods;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(Goods goods);
	
	
	/**
	 * 修改
	 */
	public void update(Goods goods);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public Goods findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long [] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbGoods goods, int pageNum,int pageSize);

	/**
	 * 商品审核
	 * @param ids
	 * @param auditStatus
	 */
	void auditGoods(Long[] ids, String auditStatus);


	/**
	 * 商品上/下架
	 * @param ids
	 * @param marketable
	 */
	void setMarketable(Long[] ids, String marketable);


	/**
	 * 根据id 查询审核通过的sku列表
	 * @param goodsIds
	 * @param status
	 * @return
	 */
	List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status);

}

package cn.itcast.web.controller;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import cn.itcast.pojo.TbItem;
import cn.itcast.service.ItemSearchService;
import cn.itcast.service.ItemService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import cn.itcast.pojo.TbGoods;
import cn.itcast.service.GoodsService;

import entity.PageResult;
import entity.Result;
import pojogroup.Goods;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Reference(timeout = 100000)
	private ItemSearchService itemSearchService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);

			//同步删除索引库中sku
			itemSearchService.deleteSolrSku(Arrays.asList(ids));

			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}

	/**
	 * 商品审核
	 * @param ids
	 * @param status
	 * @return
	 */
	@RequestMapping("/auditGoods")
	public Result auditGoods(Long[] ids,String status){

		try {
			goodsService.auditGoods(ids,status);

			if("1".equals(status)){

				List<TbItem> skuList = goodsService.findItemListByGoodsIdandStatus(ids, status);

				if(skuList.size() > 0){

					itemSearchService.importList(skuList);

				}else{
					System.out.println("没有sku明细数据");
				}

			}
			return new Result(true,"审核完成");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"审核失败");
		}

	}



	
}

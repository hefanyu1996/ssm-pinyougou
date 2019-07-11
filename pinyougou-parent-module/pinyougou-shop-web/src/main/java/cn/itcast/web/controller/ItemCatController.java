package cn.itcast.web.controller;
import cn.itcast.pojo.TbItemCat;
import cn.itcast.service.ItemCatService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

	@Reference
	private ItemCatService itemCatService;

	@RequestMapping("/findByParentId")
	public List<TbItemCat> findItemCatOptions(Long parentId){
		return itemCatService.findByParentId(parentId);
	}

	@RequestMapping("/findOne.do")
	public TbItemCat findOne(Long id){
		return itemCatService.findOne(id);
	}


	@RequestMapping("/findAll.do")
	public List<TbItemCat> findAll(){
		return itemCatService.findAll();
	}

}

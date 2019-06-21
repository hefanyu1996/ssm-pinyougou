package cn.itcast.service.impl;
import cn.itcast.dao.TbSpecificationMapper;
import cn.itcast.dao.TbSpecificationOptionMapper;
import cn.itcast.pojo.TbSpecification;
import cn.itcast.pojo.TbSpecificationExample;
import cn.itcast.pojo.TbSpecificationOption;
import cn.itcast.pojo.TbSpecificationOptionExample;
import cn.itcast.service.SpecificationService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import pojogroup.Specification;

import java.util.List;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;

	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Specification specification) {

		TbSpecification tbSpecification = specification.getSpecification();
		specificationMapper.insert(tbSpecification);
		//获取插入后的规格主键
		Long id = tbSpecification.getId();

		List<TbSpecificationOption> optionList = specification.getSpecificationOptionList();

		//循环 设置规格选项对应规格id   并插入
		for (TbSpecificationOption tbSpecificationOption : optionList) {
			tbSpecificationOption.setSpecId(id);//设置id
			specificationOptionMapper.insert(tbSpecificationOption);//新增规格
		}

	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){

		TbSpecification tbSpecification = specification.getSpecification();
		//修改规格
		specificationMapper.updateByPrimaryKey(tbSpecification);

		//删除原有规格选项
		TbSpecificationOptionExample tbSpecificationOptionExample = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = tbSpecificationOptionExample.createCriteria();
		//指定规格id为条件
		criteria.andSpecIdEqualTo(tbSpecification.getId());

		specificationOptionMapper.deleteByExample(tbSpecificationOptionExample);

		//循环插入修改的规格选项
		for (TbSpecificationOption tbSpecificationOption : specification.getSpecificationOptionList()) {
			//设置对应规格id
			tbSpecificationOption.setSpecId(tbSpecification.getId());
			specificationOptionMapper.insert(tbSpecificationOption);
		}


	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){

		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);

		TbSpecificationOptionExample tbSpecificationOptionExample = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = tbSpecificationOptionExample.createCriteria();
		criteria.andSpecIdEqualTo(id);//根据Id查询规格选项

		List<TbSpecificationOption> tbSpecificationOptionList = specificationOptionMapper.selectByExample(tbSpecificationOptionExample);
		//返回组合实体
		return new Specification(tbSpecification,tbSpecificationOptionList);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//批量删除规格
			specificationMapper.deleteByPrimaryKey(id);

			TbSpecificationOptionExample tbSpecificationOptionExample = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = tbSpecificationOptionExample.createCriteria();
			//将规格id作为条件
			criteria.andSpecIdEqualTo(id);
			//删除对应规格的所有规格选项
			specificationOptionMapper.deleteByExample(tbSpecificationOptionExample);

		}		
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		TbSpecificationExample.Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}

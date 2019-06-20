package cn.itcast.service.impl;

import cn.itcast.dao.TbBrandMapper;
import cn.itcast.pojo.TbBrand;
import cn.itcast.pojo.TbBrandExample;
import cn.itcast.service.BrandService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 品牌服务层
 */
@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper tbBrandMapper;

    /**
     * 查询所有品牌记录
     *
     * @return
     */
    public List<TbBrand> findAll() {
        return tbBrandMapper.selectByExample(new TbBrandExample());
    }


    /**
     * 分页查询 返回Page
     *
     * @param currPage
     * @param pageSize
     * @return
     */
    @Override
    public PageResult<TbBrand> findByPage(Integer currPage, Integer pageSize) {

        //查询前调用分页插件
        PageHelper.startPage(currPage, pageSize);
        //查询记录
        Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(null);
        //将总记录数 和 当前页数据进行封装 返回
        PageResult<TbBrand> tbBrandPageResult = new PageResult<TbBrand>(page.getResult(), page.getTotal());
        return tbBrandPageResult;

    }


    /**
     * 添加品牌
     *
     * @param tbBrand
     * @return
     */
    @Override
    public void save(TbBrand tbBrand) {
        tbBrandMapper.insert(tbBrand);

    }

    /**
     * 根据id修改品牌
     *
     * @param tbBrand
     */
    @Override
    public void update(TbBrand tbBrand) {
        tbBrandMapper.updateByPrimaryKey(tbBrand);
    }

    /**
     * 根据id查询品牌
     *
     * @param id
     * @return
     */
    @Override
    public TbBrand findOne(Long id) {
        return tbBrandMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     */
    @Override
    public void delete(Long[] ids) {

        for (Long id : ids) {
            tbBrandMapper.deleteByPrimaryKey(id);
        }

    }

    @Override
    public PageResult<TbBrand> findByPage(TbBrand tbBrand, Integer currPage, Integer pageSize) {

        PageHelper.startPage(currPage, pageSize);

        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        String name = tbBrand.getName();
        String firstChar = tbBrand.getFirstChar();
        if (tbBrand != null) {
            if (name != null && name.length() > 0) {
                criteria.andNameLike("%" + name + "%");
            }
            if (firstChar != null && firstChar.length() > 0) {
                criteria.andFirstCharLike("%" + firstChar + "%");
            }

        }
        Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(example);

        return new PageResult<TbBrand>(page.getResult(), page.getTotal());

    }


}

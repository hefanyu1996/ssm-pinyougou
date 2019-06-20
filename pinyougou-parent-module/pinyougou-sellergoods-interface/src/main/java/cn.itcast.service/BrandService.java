package cn.itcast.service;

import cn.itcast.pojo.TbBrand;
import entity.PageResult;

import java.util.List;

public interface BrandService {

    /**
     * 查询总记录数
     * @return
     */
    List<TbBrand> findAll();

    /**
     * 返回分页列表（分页查询）
     * @return
     */
    PageResult<TbBrand> findByPage(Integer currPage,Integer pageSize);

    /**
     * 添加品牌
     * @param tbBrand
     * @return
     */
    void save(TbBrand tbBrand);


    /**
     * 修改品牌
     * @param tbBrand
     */
    void update(TbBrand tbBrand);

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    TbBrand findOne(Long id);

    /**
     * 批量删除
     * @param ids
     */
    void delete(Long[] ids);

    /**
     * 条件分页查询
     * @param tbBrand   查询条件
     * @param currPage  当前页
     * @param pageSize  每页行数
     * @return
     */
    PageResult<TbBrand> findByPage(TbBrand tbBrand, Integer currPage, Integer pageSize);
}

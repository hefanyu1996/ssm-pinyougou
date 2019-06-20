package cn.itcast.web.controller;


import cn.itcast.pojo.TbBrand;
import cn.itcast.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll.do")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    /**
     * 分页查询
     * @param currPage 当前页
     * @param pageSize 每页行数
     * @return
     */
    @RequestMapping("/findByPage.do")
    public PageResult<TbBrand> findByPage(Integer currPage ,Integer pageSize){
        return brandService.findByPage(currPage,pageSize);

    }


    /**
     * 添加
     * @param tbBrand 品牌实体
     * @return
     */
    @RequestMapping("/save.do")
    public Result save(@RequestBody TbBrand tbBrand){

        try {
            brandService.save(tbBrand);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "服务器正忙");
        }
    }

    /**
     * 根据id查询
     * @return
     */
    @RequestMapping("/findOne.do")
    public TbBrand findOne(Long id){
        TbBrand tbBrand = brandService.findOne(id);
        return tbBrand;
    }


    /**
     * 根据id修改
     * @param tbBrand
     * @return
     */
    @RequestMapping("/update.do")
    public Result update(@RequestBody TbBrand tbBrand){
        try {
            brandService.update(tbBrand);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "服务器正忙");
        }


    }


    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/remove.do")
    public Result Remove(Long[] ids){
        try{
            brandService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"服务器正忙");
        }

    }


    @RequestMapping("/search.do")
    public PageResult<TbBrand> search(@RequestBody TbBrand tbBrand,Integer currPage,Integer pageSize ){

        return brandService.findByPage(tbBrand,currPage,pageSize);
    }





}

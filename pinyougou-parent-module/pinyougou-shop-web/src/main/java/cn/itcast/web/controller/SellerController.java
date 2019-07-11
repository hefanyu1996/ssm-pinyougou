package cn.itcast.web.controller;

import cn.itcast.pojo.TbSeller;
import cn.itcast.service.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbSeller> findAll() {
        return sellerService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return sellerService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param seller
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbSeller seller) {
        try {
            //取出密码 加密
            String password = passwordEncoder.encode(seller.getPassword());
            //设置 加密密码
            seller.setPassword(password);
            //存入
            sellerService.add(seller);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param seller
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbSeller seller) {
        try {
            sellerService.update(seller);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }


    @RequestMapping("/changePassword")
    public Result changePassword(String oldPassword, String newPassword) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();

        //获取当前登录用户信息
        TbSeller checkTbSeller = sellerService.findOne(sellerId);
        if (checkTbSeller != null) {
            //判断原密码是否正确
            boolean matches = passwordEncoder.matches(oldPassword, checkTbSeller.getPassword());

            if (matches) {
                //新密码加密
                String encodeNewPassword = passwordEncoder.encode(newPassword);
                try {
                    sellerService.changePassword(sellerId,encodeNewPassword);
                    return new Result(true, "密码修改成功");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return new Result(false, "原密码输入不正确");
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbSeller findOne(String id) {
        return sellerService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(String[] ids) {
        try {
            sellerService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param seller
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbSeller seller, int page, int rows) {
        return sellerService.findPage(seller, page, rows);
    }


}

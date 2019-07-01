package cn.itcast.test;


import cn.itcast.dao.TbItemMapper;
import cn.itcast.pojo.TbItem;
import cn.itcast.pojo.TbItemExample;
import cn.itcast.util.SolrUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/*@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext-*.xml")*/
public class SolrUtilTest {


   /* @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private SolrUtil solrUtil;*/

    /*@Test
    public void testFindItemData(){
        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andStatusEqualTo("1");

        List<TbItem> tbItemList = tbItemMapper.selectByExample(tbItemExample);

        for (TbItem tbItem : tbItemList) {
            System.out.println(tbItem.getTitle()+"-"+tbItem.getPrice());
        }
    }*/

    /*@Test
    public void testUtil(){
        solrUtil.importItemData();
    }*/


}

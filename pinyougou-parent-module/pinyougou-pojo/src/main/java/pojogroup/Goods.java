package pojogroup;

import cn.itcast.pojo.TbGoods;
import cn.itcast.pojo.TbGoodsDesc;
import cn.itcast.pojo.TbItem;

import java.io.Serializable;
import java.util.List;

/**
 * 商品组合类
 */
public class Goods implements Serializable {

    private TbGoods tbGoods;

    private TbGoodsDesc tbGoodsDesc;

    private List<TbItem> tbItemList;

    public TbGoods getTbGoods() {
        return tbGoods;
    }

    public void setTbGoods(TbGoods tbGoods) {
        this.tbGoods = tbGoods;
    }

    public TbGoodsDesc getTbGoodsDesc() {
        return tbGoodsDesc;
    }

    public void setTbGoodsDesc(TbGoodsDesc tbGoodsDesc) {
        this.tbGoodsDesc = tbGoodsDesc;
    }

    public List<TbItem> getTbItemList() {
        return tbItemList;
    }

    public void setTbItemList(List<TbItem> tbItemList) {
        this.tbItemList = tbItemList;
    }
}

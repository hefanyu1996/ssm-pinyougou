package entity;

import java.io.Serializable;
import java.util.List;

public class PageResult<T> implements Serializable {

    //总记录数
    private long total;

    //当前页数据
    private List<T> rows;

    public PageResult() {
    }

    public PageResult(List<T> rows,long total ) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}

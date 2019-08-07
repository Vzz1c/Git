package entity;

import java.io.Serializable;
import java.util.List;
/**
 * 分页结果
 * @author Vzz1c
 *
 */
public class PageResult implements Serializable {
	private long total; //总记录数
	private List rows; //当前页
	public PageResult(long total, List rows) {
		super();
		this.total = total;
		this.rows = rows;
	}
	public PageResult() {
		super();
		// TODO Auto-generated constructor stub
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	public List getRows() {
		return rows;
	}
	public void setRows(List rows) {
		this.rows = rows;
	}
	
}

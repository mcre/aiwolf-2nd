package net.mchs_u.mc.aiwolf.analysis;

//MontecalroDataから使用される、Double側でソートしたいためだけのクラス
//雑
public class StringDouble implements Comparable<StringDouble>{
	private String s = null;
	private Double d = null;

	public StringDouble(String s, Double d) {
		this.s = s;
		this.d = d;
	}

	@Override
	public int compareTo(StringDouble o) {
		if(this.d < o.d)
			return 1;
		if(this.d > o.d)
			return -1;
		return 0;
	}

	public String getString() {
		return s;
	}

	public Double getDouble() {
		return d;
	}

	@Override
	public String toString() {
		return "StringDouble [s=" + s + ", d=" + d + "]";
	}

}

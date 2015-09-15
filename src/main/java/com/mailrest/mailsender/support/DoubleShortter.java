package com.mailrest.mailsender.support;

public final class DoubleShortter {

	public static double shortter(double val) {
		return shortter(val, 3);
	}
	
	public static double shortter(double val, int precision) {
	
		double aval = Math.abs(val);
		
		double pow = pow10(precision);
		
		for (double i = pow; ; i /= 10.0) {
			if (aval > i || i < 0.1) {
				double m = pow / i;
				return Math.round(val * m) / m;
			}
		}

	}
	
	public static void append(StringBuilder str, double val) {
		append(str, val, 5);
	}
	
	private static double pow10(int precision) {
		double pow = 1.0;
		for (int i = 0; i != precision; ++i) {
			pow *= 10.0;
		}
		return pow;
	}
	
	public static void append(StringBuilder str, double val, int precision) {
		
		if (val < 0.0) {
			str.append('-');
			val *= -1.0;
		}
		
		double pow = pow10(precision);
		
	    double mval = val * pow;
	    long lval = (long) mval;
	    
	    String sval = Long.toString(lval);
	    ZeroBufferedStringBuilder buff = new ZeroBufferedStringBuilder(str);

	    if (sval.length() <= precision) {
	    	buff.append('0');
	    	buff.append('.');
	    	for (int i = 0; i != precision-sval.length(); ++i) {
	    		buff.append('0');
	    	}
		    for (int i = 0; i != sval.length(); ++i) {
		    	buff.append(sval.charAt(i));
		    }
	    }
	    else {
		    for (int i = 0; i != sval.length(); ++i) {
		    	if (sval.length() - i == precision) {
		    		buff.append('.');
		    	}
		    	buff.append(sval.charAt(i));
		    }
	    }
	    

	}
	
	private final static class ZeroBufferedStringBuilder {
		
		private StringBuilder str;
		private boolean dot = false;
		private boolean firstDot = true;
		private int zeros = 0;
		
		public ZeroBufferedStringBuilder(StringBuilder str) {
			this.str = str;
		}
		
		void append(char ch) {
			if (dot) {
				if (ch == '0') {
					zeros++;
					return;
				}
				if (firstDot) {
					str.append('.');
					firstDot = false;
				}
				while(zeros > 0) {
					str.append('0');
					zeros--;
				}
			}
			if (ch == '.') {
				dot = true;
				if (firstDot) {
					return;
				}
			}
			str.append(ch);
		}
		
	}
	
}

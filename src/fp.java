public class fp
{

    public String myName()
    {
        return "Benjamin Chavez";
    }

    public int add(int a, int b)
    {
        FPNumber fa = new FPNumber(a);
        FPNumber fb = new FPNumber(b);
        FPNumber result = new FPNumber(0);

        if(fa.isNaN() || fb.isNaN()){
            result.setS(1);
            result.setE(255);
            result.setF(12);
            return result.asInt();
        }
        if(fa.isZero()){
            return fb.asInt();
        }
        if(fb.isZero()){
            return fa.asInt();
        }
        if(fa.isInfinity()){
            if(fb.isInfinity()){
                if(fa.s() == fb.s()){
                    result.setS(fa.s());
                    result.setE(255);
                    result.setF(0);
                    return result.asInt();
                }
                else{
                    result.setS(1);
                    result.setE(255);
                    result.setF(12);
                }
            }
            return fa.asInt();
        }
        if(fb.isInfinity()){
            return fb.asInt();
        }

        FPNumber greater = new FPNumber(0);
        FPNumber lesser = new FPNumber(0);

        if(fa.e() >= fb.e()){
            greater = fa;
            lesser = fb;
        }
        else{
            greater = fb;
            lesser = fa;
        }

        long lesserfractal = lesser.f();
        long greaterfractal = greater.f();
        int lesserex = lesser.e();
        int greaterex = greater.e();

        while(lesserex < greaterex){
            lesserfractal = (lesserfractal >> 1);
            lesserex++;
        }

        if(lesser.s() == -1 && greater.s() == 1 || greater.s() == -1 && lesser.s() == 1){
            lesserfractal = lesserfractal * -1;
        }

        long totalfract = lesserfractal + greaterfractal;

        if(((totalfract >> 26) & 1) == 1){
            totalfract = (totalfract >>> 1);
            greaterex++;
        }
        if(greaterex >= 255){
            result.setS(greater.s());
            result.setE(255);
            result.setF(0);
            return result.asInt();
        }

        while(((totalfract >> 25) & 1) == 0){
            totalfract = (totalfract << 1);
            greaterex--;
        }

        result.setS(greater.s());
        result.setE(greaterex);
        result.setF(totalfract);
        return result.asInt();
    }

    public int mul(int a, int b)
    {
        FPNumber fa = new FPNumber(a);
        FPNumber fb = new FPNumber(b);
        FPNumber result = new FPNumber(0);

        if(fa.isNaN()){
            return fa.asInt();
        }
        if(fb.isNaN()){
            return fb.asInt();
        }
        if(fa.isZero()){
            if(fb.isInfinity()){
                result.setS(1);
                result.setE(255);
                result.setF(12);
                return result.asInt();
            }
            else{
                return 0;
            }
        }
        if(fb.isZero()){
            if(fa.isInfinity()){
                result.setS(1);
                result.setE(255);
                result.setF(12);
                return result.asInt();
            }
            else{
                return 0;
            }
        }
        if(fa.isInfinity() || fb.isInfinity()){
            result.setS(1);
            result.setE(255);
            result.setF(0);
            return result.asInt();
        }

        int totalex = (fa.e() + fb.e()) - 127;

        if(totalex > 256){
            return(0/0);
        }
        else if(totalex < 0){
            return 0;
        }

        long multfract = fa.f() * fb.f();

        if(fa.s() == 1 && fb.s() == -1 || fa.s() == -1 && fb.s() == 1){
            result.setS(-1);
        }


        multfract = (multfract >>> 25);
        if((((multfract >> 26) & 1))== 1){
            multfract = (multfract >>> 1);
            totalex++;
        }

        result.setE(totalex);
        result.setF(multfract);

        return result.asInt();
    }

    // Here is some test code that one student had written...
    public static void main(String[] args)
    {
        int v24_25	= 0x41C20000; // 24.25
        int v_1875	= 0xBE400000; // -0.1875
        int v5		= 0xC0A00000; // -5.0

        fp m = new fp();

        System.out.println(Float.intBitsToFloat(m.add(v24_25, v_1875)) + " should be 24.0625");
        System.out.println(Float.intBitsToFloat(m.add(v24_25, v5)) + " should be 19.25");
        System.out.println(Float.intBitsToFloat(m.add(v_1875, v5)) + " should be -5.1875");

        System.out.println(Float.intBitsToFloat(m.mul(v24_25, v_1875)) + " should be -4.546875");
        System.out.println(Float.intBitsToFloat(m.mul(v24_25, v5)) + " should be -121.25");
        System.out.println(Float.intBitsToFloat(m.mul(v_1875, v5)) + " should be 0.9375");
    }

}
class FPNumber
{
    int _s, _e;
    long _f;

    public FPNumber(int a)
    {
        _s = (((a >> 31) & 1) == 1) ? -1 : 1;
        _e = (a >> 23) & 0xFF;
        _f = a & 0x7FFFFF;
        if (_e != 0 && _e != 255)
        {
            _f |= 0x0800000;
        }
        _f <<= 2;
    }

    public int s()
    {
        return _s;
    }

    public int e()
    {
        return _e;
    }

    public long f()
    {
        return _f;
    }

    public void setS(int val)
    {
        _s = val;
    }

    public void setE(int val)
    {
        _e = val;
    }

    public void setF(long val)
    {
        _f = val;
    }

    public boolean isNaN()
    {
        return _e == 255 && _f > 0;
    }

    public boolean isInfinity()
    {
        return _e == 255 && _f == 0;
    }

    public boolean isZero()
    {
        return _e == 0 && _f == 0;
    }

    public int asInt()
    {
        return ((_s == -1) ? 0x80000000 : 0) | (_e << 23) | (((int) _f >> 2) & 0x07FFFFF);
    }
}

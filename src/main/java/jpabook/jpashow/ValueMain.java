package jpabook.jpashow;

public class ValueMain {
    public static void main(String[] args) {
        Integer a = 10;
        Integer b = a; // reference 주소값 가져감

        System.out.println("a = " + a);
        System.out.println("b = " + b); // reference가 넘어가서 같은 instance 공유함
    }
}

package xyz.dsvshx.ioc.entity;

/**
 * @author dongzhonghua
 * Created on 2021-03-10
 */
public class Robot {
    //需要注入 hand 和 mouth
    private Hand hand;
    private Mouth mouth;

    public void show(){
        hand.waveHand();
        mouth.speak();
    }
}

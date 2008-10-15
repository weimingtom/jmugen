package org.lee.mugen.sprite.cns.type.function;

import org.lee.mugen.core.StateMachine;
import org.lee.mugen.parser.type.Valueable;
import org.lee.mugen.sprite.character.Sprite;
import org.lee.mugen.sprite.cns.eval.function.StateCtrlFunction;
import org.lee.mugen.sprite.parser.ExpressionFactory;
import org.lee.mugen.sprite.parser.Parser;

/**
 * Ok
 * @author Dr Wong
 *
 */
public class Angledraw extends StateCtrlFunction {

    public Angledraw() {
        super("angledraw", new String[] {"scale"});
    }
    @Override
    public Object getValue(String spriteId, Valueable... params) {
		int scaleIndex = getParamIndex("scale");
		if (valueableParams[scaleIndex] != null) {
			Valueable xscale = valueableParams[scaleIndex][0];
			float fXScale = Parser.getFloatValue(xscale.getValue(spriteId, params));
			Sprite sprite = StateMachine.getInstance().getSpriteInstance(
					spriteId);
			sprite.getSprAnimMng().getSpriteDrawProperties().setXScale(fXScale);
			if (valueableParams[scaleIndex].length > 1) {
				Valueable yscale = valueableParams[scaleIndex][0];
				float fYScale = Parser.getFloatValue(yscale.getValue(spriteId, params));
				sprite.getSprAnimMng().getSpriteDrawProperties().setYScale(fYScale);
			}
			
		}
		StateMachine.getInstance().getSpriteInstance(spriteId).getSprAnimMng().getSpriteDrawProperties().setActive(true);
		return null;
    }
    
	public static Valueable[] parse(String name, String value) {
		String[] tokens = ExpressionFactory.expression2Tokens(value);
		return ExpressionFactory.evalExpression(tokens);
	}
}
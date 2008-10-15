package org.lee.mugen.sprite.cns.type.function;

import org.lee.mugen.core.StateMachine;
import org.lee.mugen.parser.type.Valueable;
import org.lee.mugen.sprite.character.Sprite;
import org.lee.mugen.sprite.cns.eval.function.StateCtrlFunction;
import org.lee.mugen.sprite.parser.ExpressionFactory;
import org.lee.mugen.sprite.parser.Parser;

public class Targetfacing extends StateCtrlFunction {

    // TODO : targetfacing
    public Targetfacing() {
        super("targetfacing", new String[] {"id", "value"});
    }

	@Override
	public Object getValue(String spriteId, Object bean, String prefixFunction, Valueable... params) {
		int idIndex = getParamIndex("id");
		String id = null;
		if (valueableParams[idIndex] != null) {
			Valueable vId = valueableParams[idIndex][0];
			if (vId != null) {
				id = vId.getValue(spriteId).toString();
			}
		}
		
		boolean isSameFlip;
		int valueIndex = getParamIndex("value");
		Valueable valueId = valueableParams[valueIndex][0];
		isSameFlip = Parser.getIntValue(valueId.getValue(spriteId)) > 0;

		Sprite sprite = StateMachine.getInstance().getSpriteInstance(spriteId);
		boolean isFlip = sprite.getInfo().isFlip();
		isFlip = isSameFlip? isFlip: !isFlip;
		Sprite s = StateMachine.getInstance().getFightEngine().getTarget(sprite, Integer.parseInt(id == null? -1 + "": id));
		if (s != null) {
			s.getInfo().setFlip(isFlip);
		}
		return null;
	}

	public static Valueable[] parse(String name, String value) {
		String[] tokens = ExpressionFactory.expression2Tokens(value);
		return ExpressionFactory.evalExpression(tokens);
	}	
    
}
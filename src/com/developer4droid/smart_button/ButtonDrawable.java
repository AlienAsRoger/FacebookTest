package com.developer4droid.smart_button;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import com.developer4droid.FacebookTest.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: roger sent2roger@gmail.com
 * Date: 12.05.13
 * Time: 18:40
 */
public class ButtonDrawable extends StateListDrawable {

	static final int DEFAULT_RADIUS = 4;
	static final int DEFAULT_BEVEL_INSET = 2;

	static final int TOP_BOTTOM = 0;
	static final int TR_BL = 1;
	static final int RIGHT_LEFT = 2;
	static final int BR_TL = 3;
	static final int BOTTOM_TOP = 4;
	static final int BL_TR = 5;
	static final int LEFT_RIGHT = 6;
	static final int TL_BR = 7;


	static final int TRANSPARENT = 0x00000000;
	public static final int DEF_VALUE = -1;
	static final int[] PRESSED_STATE = new int[]{android.R.attr.state_pressed};
	static final int[] ENABLED_STATE = new int[]{android.R.attr.state_enabled};

	ColorFilter enabledFilter;
	ColorFilter disabledFilter;
	ColorFilter pressedFilter;
	ColorFilter selectedFilter;
	ColorFilter checkedFilter;
	int disabledAlpha;
	int enabledAlpha;

	LayerDrawable enabledDrawable;
	LayerDrawable pressedDrawable;

	float[] outerRect;
	RectF bevelRect;
	RectF glassyRect;

	int buttonIndex;

	/* Button parameter */
	int colorOuterBorder;
	int colorBottom;
	int colorRight;
	int colorLeft;
	int colorTop;
	int colorBottom2;
	int colorRight2;
	int colorLeft2;
	int colorTop2;
	int colorSolid;
	int colorGradientStart;
	int colorGradientCenter;
	int colorGradientEnd;

	int colorBottomP;
	int colorRightP;
	int colorLeftP;
	int colorTopP;
	int colorBottom2P;
	int colorRight2P;
	int colorLeft2P;
	int colorTop2P;
	int colorSolidP;
	int colorGradientStartP;
	int colorGradientCenterP;
	int colorGradientEndP;

	int gradientAngle;
	boolean isSolid;
	boolean isGlassy;
	boolean useBorder;
	boolean usePressedLayer;
	int bevelLvl;
	int bevelInset;
	int radius;
	int bevelSize;

	/* Padding */
	int padding;
	int leftPadding;
	int topPadding;
	int rightPadding;
	int bottomPadding;
	int userPaddingLeftInitial;
	int userPaddingRightInitial;
	boolean leftPaddingDefined;
	boolean rightPaddingDefined;


	/* state & other values */
	private boolean initialized;
	private static final int glassyBorderIndex = 0;
	int glassyBevelSize;

	static InsetInfo insetOne = new InsetInfo();
	static InsetInfo insetTwo = new InsetInfo();
	static {
		insetOne.top = new int[]{0, 0, 0, 1};
		insetOne.left = new int[]{1, 0, 0, 1};
		insetOne.right = new int[]{1, 1, 0, 1};
		insetOne.bottom = new int[]{1, 1, 1, 0};
		insetOne.button = new int[]{1, 1, 1, 1};

		insetTwo.top = new int[]{0, 0, 0, 2};
		insetTwo.left = new int[]{2, 0, 0, 2};
		insetTwo.right = new int[]{2, 2, 0, 2};
		insetTwo.bottom = new int[]{2, 2, 2, 0};
		insetTwo.button = new int[]{2, 2, 2, 2};
	}

	/**
	 * Use for init ButtonDrawableBuilder
	 */
	ButtonDrawable() {
		setDefaults();
	}

	public ButtonDrawable(Context context, AttributeSet attrs) {
		Resources resources = context.getResources();

		setDefaults();

		parseAttributes(context, attrs);

	    init(resources);
	}

	private void setDefaults() {
		// defaults
		bevelLvl = 1;
		isSolid = true;
		radius = DEFAULT_RADIUS;

		disabledAlpha = 100;
		enabledAlpha = 0xFF;
	}

	void init(Resources resources) {
		if (radius > 0) {
			outerRect = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};
		} else {
			outerRect = null; // solve optimization problem because it uses mPath.addRect instead of mPath.addRoundRect
		}

		bevelSize = resources.getDimensionPixelSize(R.dimen.default_bevel_size);
		bevelRect = new RectF(bevelSize, bevelSize, bevelSize, bevelSize);

		if (isGlassy) {
			glassyBevelSize = resources.getDimensionPixelSize(R.dimen.default_bevel_glassy_size);
			glassyRect = new RectF(glassyBevelSize, glassyBevelSize, glassyBevelSize, glassyBevelSize);

			int pressedOverlay = resources.getColor(R.color.glassy_button_overlay_p);
			int selectedOverlay = resources.getColor(R.color.glassy_button_overlay_s);
			pressedFilter = new PorterDuffColorFilter(pressedOverlay, PorterDuff.Mode.MULTIPLY);
			selectedFilter = new PorterDuffColorFilter(selectedOverlay, PorterDuff.Mode.MULTIPLY);
		} else {
			int pressedOverlay = resources.getColor(R.color.default_button_overlay_p);
			int selectedOverlay = resources.getColor(R.color.default_button_overlay_s);
			pressedFilter = new PorterDuffColorFilter(pressedOverlay, PorterDuff.Mode.SRC_ATOP);
			selectedFilter = new PorterDuffColorFilter(selectedOverlay, PorterDuff.Mode.SRC_ATOP);
		}
		int checkedOverlay = resources.getColor(R.color.default_button_overlay_c);
		checkedFilter = new PorterDuffColorFilter(checkedOverlay, PorterDuff.Mode.SRC_ATOP);

//		pressedFilter = new PorterDuffColorFilter(PRESSED_OVERLAY, PorterDuff.Mode.SCREEN); // bad edges
//		pressedFilter = new PorterDuffColorFilter(PRESSED_OVERLAY, PorterDuff.Mode.SRC_IN); //  make transparent  - dark - bad
//		pressedFilter = new PorterDuffColorFilter(PRESSED_OVERLAY, PorterDuff.Mode.SRC_OUT); // bad edges
//		pressedFilter = new PorterDuffColorFilter(PRESSED_OVERLAY, PorterDuff.Mode.DST_IN); // make light transparent
//		pressedFilter = new PorterDuffColorFilter(PRESSED_OVERLAY, PorterDuff.Mode.DARKEN);  // bad edges
//		pressedFilter = new PorterDuffColorFilter(PRESSED_OVERLAY, PorterDuff.Mode.MULTIPLY); // make transparent  - dark - bad
//		pressedFilter = new PorterDuffColorFilter(PRESSED_OVERLAY, PorterDuff.Mode.OVERLAY); // bad edges
//		pressedFilter = new PorterDuffColorFilter(PRESSED_OVERLAY, PorterDuff.Mode.XOR);  // bad edges

		List <LayerInfo> enabledLayers = new ArrayList<LayerInfo>();
		List<LayerInfo> pressedLayers = new ArrayList<LayerInfo>();

		if (useBorder) { // outer border
			int strokeSize = resources.getDimensionPixelSize(R.dimen.default_stroke_width);

			RectF stroke = new RectF(strokeSize, strokeSize, strokeSize, strokeSize);

			RectShape rectShape = new RoundRectShapeFixed(outerRect, stroke, outerRect);
			ShapeDrawable shapeDrawable = new ShapeDrawable(rectShape);
			shapeDrawable.getPaint().setColor(colorOuterBorder);

			enabledLayers.add(new LayerInfo(shapeDrawable, 0, 0, 0, 0));
			if (usePressedLayer) {
				pressedLayers.add(new LayerInfo(shapeDrawable, 0, 0, 0, 0));
			}
		}

		createDefaultState(enabledLayers);

		if (usePressedLayer) { // by default we apply color filter and alpha for different states
			createPressedState(pressedLayers);
		}
	}

	@Override
	public void draw(Canvas canvas) {
		if (!initialized) {
			iniLayers(canvas);
		}
		super.draw(canvas);
	}

	void iniLayers(Canvas canvas) {
		int width = canvas.getWidth();
		int height = canvas.getHeight();
		if (!isSolid) {
			((ShapeDrawable) enabledDrawable.getDrawable(buttonIndex)).getPaint().setShader(
					makeLinear(width, height, colorGradientStart, colorGradientCenter, colorGradientEnd));
			if (usePressedLayer) {
				((ShapeDrawable) pressedDrawable.getDrawable(buttonIndex)).getPaint().setShader(
						makeLinear(width, height, colorGradientStartP, colorGradientCenterP, colorGradientEndP));
			}
		}

		if (isGlassy) {
			((ShapeDrawable) enabledDrawable.getDrawable(glassyBorderIndex)).getPaint().setShader(
					makeLinear(width, height, colorGradientStart, colorGradientCenter, colorGradientEnd));
		}

		initialized = true;
	}

	void createDefaultState(List<LayerInfo> enabledLayers) {   // TODO it can be improved!
		if (isGlassy) { // create thin gradient border to mimic bevel // TODO make every layer gradient? to reduce total number of layers...
			ShapeDrawable drawable = new ShapeDrawable(new RoundRectShape(outerRect, glassyRect, outerRect));
			enabledLayers.add(new LayerInfo(drawable, bevelInset + insetOne.top[0], bevelInset + insetOne.top[1],
					bevelInset + insetOne.top[2], bevelInset + insetOne.top[3]));
		} else {
			createLayer(colorTop, insetOne.top, enabledLayers);
			createLayer(colorBottom, insetOne.bottom, enabledLayers); // order is important
			createLayer(colorLeft, insetOne.left, enabledLayers);
			createLayer(colorRight, insetOne.right, enabledLayers);

			if (bevelLvl == 2) {
				createLayer(colorTop2, insetTwo.top, enabledLayers);
				createLayer(colorBottom2, insetTwo.bottom, enabledLayers);
				createLayer(colorLeft2, insetTwo.left, enabledLayers);
				createLayer(colorRight2, insetTwo.right, enabledLayers);
			}
		}

		int[] button = bevelLvl == 1 ? insetOne.button : insetTwo.button;
		int	color = isSolid ? colorSolid : TRANSPARENT;
		createLayer(color, button, enabledLayers, true);

		int levelCnt = enabledLayers.size();
		buttonIndex = levelCnt - 1;

		Drawable[] enabledDrawables = new Drawable[levelCnt];  // TODO improve that mess
		for (int i = 0; i < levelCnt; i++) {
			LayerInfo layerInfo = enabledLayers.get(i);
			enabledDrawables[i] = layerInfo.shapeDrawable;
		}
		enabledDrawable = new LayerDrawable(enabledDrawables);
		for (int i = 0; i < levelCnt; i++) { // start from 2nd level, first is shadow
			LayerInfo layer = enabledLayers.get(i);
			enabledDrawable.setLayerInset(i, layer.leftInSet, layer.topInSet, layer.rightInSet, layer.bottomInSet);
		}

		addState(ENABLED_STATE, enabledDrawable);
	}

	void createPressedState(List<LayerInfo> pressedLayers) {
		createLayer(colorTopP, insetOne.top, pressedLayers);
		createLayer(colorBottomP, insetOne.bottom, pressedLayers);
		createLayer(colorLeftP, insetOne.left, pressedLayers);
		createLayer(colorRightP, insetOne.right, pressedLayers);

		if (bevelLvl == 2) {
			createLayer(colorTop2P, insetTwo.top, pressedLayers);
			createLayer(colorBottom2P, insetTwo.bottom, pressedLayers);
			createLayer(colorLeft2P, insetTwo.left, pressedLayers);
			createLayer(colorRight2P, insetTwo.right, pressedLayers);
		}

		int[] button = bevelLvl == 1 ? insetOne.button : insetTwo.button;
		int	color = isSolid ? colorSolidP : TRANSPARENT;
		createLayer(color, button, pressedLayers, true);

		int levelCnt = pressedLayers.size();
		Drawable[] pressedDrawables = new Drawable[levelCnt]; // TODO improve that mess
		for (int i = 0; i < levelCnt; i++) {
			LayerInfo layerInfo = pressedLayers.get(i);
			pressedDrawables[i] = layerInfo.shapeDrawable;
		}
		pressedDrawable = new LayerDrawable(pressedDrawables);
		for (int i = 0; i < levelCnt; i++) { // start from 2nd level, first is shadow
			LayerInfo layer = pressedLayers.get(i);
			pressedDrawable.setLayerInset(i, layer.leftInSet, layer.topInSet, layer.rightInSet, layer.bottomInSet);
		}

		addState(PRESSED_STATE, pressedDrawable);
	}

	void createLayer(int color, int[] inSet, List<LayerInfo> layers) {
		if (color != TRANSPARENT) {
			createLayer(color, inSet, layers, false);
		}
	}

	void createLayer(int color, int[] inSet, List<LayerInfo> layers, boolean isButton) {
		ShapeDrawable drawable;
		if (isButton) {
			drawable = new ShapeDrawable(new RoundRectShapeFixed(outerRect, null, null));
			setPaddingToShape(drawable);
		} else {
			drawable = new ShapeDrawable(new RoundRectShapeFixed(outerRect, bevelRect, outerRect));
		}
		if (color != TRANSPARENT) {  // TODO adjust proper logic for transparent solid use
			drawable.getPaint().setColor(color);
		}
		layers.add(new LayerInfo(drawable, bevelInset + inSet[0], bevelInset + inSet[1], bevelInset + inSet[2], bevelInset + inSet[3]));
	}

	/**
	 * Set padding to internal cover only shape of LayerDrawables
	 * @param drawable to which we set padding must be ShapeDrawable
	 */
	void setPaddingToShape(ShapeDrawable drawable) {
		if (padding != DEF_VALUE) {
			userPaddingLeftInitial = padding;
			userPaddingRightInitial = padding;
			leftPaddingDefined = true;
			rightPaddingDefined = true;
		}

		if (leftPadding != DEF_VALUE) {
			userPaddingLeftInitial = leftPadding;
			leftPaddingDefined = true;
		}

		if (rightPadding != DEF_VALUE) {
			userPaddingRightInitial = rightPadding;
			rightPaddingDefined = true;
		}

		int leftPad = padding;
		int topPad = padding;
		int rightPad = padding;
		int bottomPad = padding;
		if (leftPaddingDefined) {
			leftPad = userPaddingLeftInitial;
		}
		if (rightPaddingDefined) {
			rightPad = userPaddingRightInitial;
		}
		drawable.setPadding(leftPad, topPad, rightPad, bottomPad);
	}

	Shader makeLinear(int width, int height, int startColor, int centerColor, int endColor) {
		RectF r = new RectF(0, 0, width, height);
		float x0, x1, y0, y1;
		switch (gradientAngle) {
			case TOP_BOTTOM:
				x0 = r.left;
				y0 = r.top;
				x1 = x0;
				y1 = r.bottom;
				break;
			case TR_BL:
				x0 = r.right;
				y0 = r.top;
				x1 = r.left;
				y1 = r.bottom;
				break;
			case RIGHT_LEFT:
				x0 = r.right;
				y0 = r.top;
				x1 = r.left;
				y1 = y0;
				break;
			case BR_TL:
				x0 = r.right;
				y0 = r.bottom;
				x1 = r.left;
				y1 = r.top;
				break;
			case BOTTOM_TOP:
				x0 = r.left;
				y0 = r.bottom;
				x1 = x0;
				y1 = r.top;
				break;
			case BL_TR:
				x0 = r.left;
				y0 = r.bottom;
				x1 = r.right;
				y1 = r.top;
				break;
			case LEFT_RIGHT:
				x0 = r.left;
				y0 = r.top;
				x1 = r.right;
				y1 = y0;
				break;
			default:/* TL_BR */
				x0 = r.left;
				y0 = r.top;
				x1 = r.right;
				y1 = r.bottom;
				break;
		}

		return new LinearGradient(x0, y0, x1, y1,
				new int[]{startColor, /*centerColor,*/ endColor},
				null,
				Shader.TileMode.CLAMP);
	}

	@Override
	protected boolean onStateChange(int[] states) {
		boolean enabled = false;
		boolean pressed = false;
		boolean selected = false;
		boolean checked = false;

		for (int state : states) {
			if (state == android.R.attr.state_enabled)
				enabled = true;
			else if (state == android.R.attr.state_pressed)
				pressed = true;
			else if (state == android.R.attr.state_selected)
				selected = true;
			else if (state == android.R.attr.state_checked)
				checked = true;
		}

		Drawable drawable = mutate();
		if (enabled && pressed) {
			drawable.setColorFilter(pressedFilter);
			drawable.setAlpha(enabledAlpha);
		} else if (enabled && selected) {
			drawable.setColorFilter(selectedFilter);
			drawable.setAlpha(enabledAlpha);
		} else if (enabled && checked) {
			drawable.setColorFilter(checkedFilter);
			drawable.setAlpha(enabledAlpha);
		} else if (!enabled) {
			drawable.setColorFilter(disabledFilter);
			drawable.setAlpha(disabledAlpha);
		} else {
			drawable.setColorFilter(enabledFilter);
			drawable.setAlpha(enabledAlpha);
		}

		invalidateSelf();// need to update for pre-HC

		return super.onStateChange(states);
	}

	protected void parseAttributes(Context context, AttributeSet attrs) {
		// get style
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoboButton);
		if (array == null) {
			return;
		}

		try { // values
			radius = array.getDimensionPixelSize(R.styleable.RoboButton_btn_radius, DEFAULT_RADIUS);
			isSolid = array.getBoolean(R.styleable.RoboButton_btn_is_solid, true);
			isGlassy = array.getBoolean(R.styleable.RoboButton_btn_is_glassy, false);
			useBorder = array.getBoolean(R.styleable.RoboButton_btn_use_border, true);
			usePressedLayer = array.getBoolean(R.styleable.RoboButton_btn_use_pressed_layer, false);
			gradientAngle = array.getInt(R.styleable.RoboButton_btn_gradient_angle, TL_BR);
			bevelLvl = array.getInt(R.styleable.RoboButton_btn_bevel_lvl, 1);
			bevelInset = array.getDimensionPixelSize(R.styleable.RoboButton_btn_bevel_inset, DEFAULT_BEVEL_INSET);

			// Colors for bevel
			colorOuterBorder = array.getColor(R.styleable.RoboButton_btn_outer_border, TRANSPARENT);
			colorTop = array.getColor(R.styleable.RoboButton_btn_top, TRANSPARENT);
			colorLeft = array.getColor(R.styleable.RoboButton_btn_left, TRANSPARENT);
			colorRight = array.getColor(R.styleable.RoboButton_btn_right, TRANSPARENT);
			colorBottom = array.getColor(R.styleable.RoboButton_btn_bottom, TRANSPARENT);

			if (bevelLvl == 2) {
				// Level 2 for bevel
				colorTop2 = array.getInt(R.styleable.RoboButton_btn_top_2, TRANSPARENT);
				colorLeft2 = array.getInt(R.styleable.RoboButton_btn_left_2, TRANSPARENT);
				colorRight2 = array.getInt(R.styleable.RoboButton_btn_right_2, TRANSPARENT);
				colorBottom2 = array.getInt(R.styleable.RoboButton_btn_bottom_2, TRANSPARENT);
			}
			// Button colors
			colorSolid = array.getColor(R.styleable.RoboButton_btn_solid, TRANSPARENT);  // TODO restore
//			colorSolid = 0x33100000;
			colorGradientStart = array.getColor(R.styleable.RoboButton_btn_gradient_start, TRANSPARENT);
			colorGradientCenter = array.getColor(R.styleable.RoboButton_btn_gradient_center, TRANSPARENT);
			colorGradientEnd = array.getColor(R.styleable.RoboButton_btn_gradient_end, TRANSPARENT);

			if (usePressedLayer) {
				/* ---------------------- Pressed states colors -------------------------------------------*/
				colorTopP = array.getInt(R.styleable.RoboButton_btn_top_p, TRANSPARENT);
				colorLeftP = array.getInt(R.styleable.RoboButton_btn_left_p, TRANSPARENT);
				colorRightP = array.getInt(R.styleable.RoboButton_btn_right_p, TRANSPARENT);
				colorBottomP = array.getInt(R.styleable.RoboButton_btn_bottom_p, TRANSPARENT);

				if (bevelLvl == 2) {
					// Level 2 Pressed
					colorTop2P = array.getInt(R.styleable.RoboButton_btn_top_2_p, TRANSPARENT);
					colorLeft2P = array.getInt(R.styleable.RoboButton_btn_left_2_p, TRANSPARENT);
					colorRight2P = array.getInt(R.styleable.RoboButton_btn_right_2_p, TRANSPARENT);
					colorBottom2P = array.getInt(R.styleable.RoboButton_btn_bottom_2_p, TRANSPARENT);
				}
				// Button colors Pressed
				colorSolidP = array.getInt(R.styleable.RoboButton_btn_solid_p, TRANSPARENT);
				colorGradientStartP = array.getInt(R.styleable.RoboButton_btn_gradient_start_p, TRANSPARENT);
				colorGradientCenterP = array.getInt(R.styleable.RoboButton_btn_gradient_center_p, TRANSPARENT);
				colorGradientEndP = array.getInt(R.styleable.RoboButton_btn_gradient_end_p, TRANSPARENT);
			}

			parseDefaultAttrs(context, attrs);
		} finally {
			array.recycle();
		}
	}

	void parseDefaultAttrs(Context context, AttributeSet attrs) {
		int PADDING_INDEX = 0;
		int PADDING_LEFT_INDEX = 1;
		int PADDING_TOP_INDEX = 2;
		int PADDING_RIGHT_INDEX = 3;
		int PADDING_BOTTOM_INDEX = 4;
		int[] defaultPadding = {android.R.attr.padding, android.R.attr.paddingLeft, android.R.attr.paddingTop, android.R.attr.paddingRight, android.R.attr.paddingBottom};
		TypedArray array = context.obtainStyledAttributes(attrs, defaultPadding);
		if (array == null) {
			return;
		}
		padding = array.getDimensionPixelSize(PADDING_INDEX, DEF_VALUE);
		if (padding != DEF_VALUE) {
			userPaddingLeftInitial = padding;
			userPaddingRightInitial = padding;
			leftPaddingDefined = true;
			rightPaddingDefined = true;
		}

		leftPadding = array.getDimensionPixelSize(PADDING_LEFT_INDEX, DEF_VALUE);
		if (leftPadding != DEF_VALUE) {
			userPaddingLeftInitial = leftPadding;
			leftPaddingDefined = true;
		}

		topPadding = array.getDimensionPixelSize(PADDING_TOP_INDEX, DEF_VALUE);

		rightPadding = array.getDimensionPixelSize(PADDING_RIGHT_INDEX, DEF_VALUE);
		if (rightPadding != DEF_VALUE) {
			userPaddingRightInitial = rightPadding;
			rightPaddingDefined = true;
		}

		bottomPadding = array.getDimensionPixelSize(PADDING_BOTTOM_INDEX, DEF_VALUE);
	}

	class LayerInfo {
		int leftInSet;
		int topInSet;
		int rightInSet;
		int bottomInSet;
		ShapeDrawable shapeDrawable;

		public LayerInfo(ShapeDrawable shapeDrawable, int leftInSet, int topInSet, int rightInSet, int bottomInSet) {
			this.shapeDrawable = shapeDrawable;
			this.leftInSet = leftInSet;
			this.topInSet = topInSet;
			this.rightInSet = rightInSet;
			this.bottomInSet = bottomInSet;
		}
	}

	/**
	 * Help class to set insets for every item in layer list drawable
	 */
	static class InsetInfo {
		int[] top;
		int[] left;
		int[] right;
		int[] bottom;
		int[] button;
	}

}

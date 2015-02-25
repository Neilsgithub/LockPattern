package com.lockpattern.app;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class LockPatternView extends View{
	
	/*-------------------------------������������---------------------------------*/
	//�Ȱ���9������������������û�г�ʼ��
	private Point[][] points = new Point[3][3];
	
	//һ�������ͱ����жϵ��Ƿ��Ѿ�������ʼ����
	private boolean isInit;
	
	//���������ֱ������洢���ֵĿ�͸�
	private float width;
	private float height;
	
	//���������������ڼ�¼X��Y�����ϵ�ƫ����
	private float offsetX;
	private float offsetY;
	
	//����һЩBitmap�������洢ͼƬ��Դ
	private Bitmap pointNormal;
	private Bitmap pointPressed;
	private Bitmap pointError;
	private Bitmap linePressed;
	private Bitmap lineError;
	
	//����Ļ�ϻ�ͼ����Ҫ����
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	//Ϊ��׼ȷ���û��ʵ���㣬�ȶ����һ��ͼ��뾶
	private float bitmapR;
	
	//Ϊ�˻�����͵�֮������ߣ�������һ����ļ��ϱ�����һ��һЩ�㱻���£��Ͱ���Щ��Ž�һ�������У�Ȼ��������������ߡ��������pointList���ǰ��µĵ�ļ���
	private List<Point> pointList = new ArrayList<Point>();
	
	//Ϊ�˼�¼����ƶ������꣬������������
	private float movingX;
	private float movingY;
	
	//�����жϵ��Ƿ��Ѿ���ѡ���
	private boolean isSelect;
	
	//�����жϵ��ѡ���Ƿ����
	private boolean isFinish;
	
	private static final int POINT_SINZE = 4;
	
	//������ƶ������ǲ��ǾŹ�������ĵ�
	private boolean movingNoPoint;
	
	
	private Matrix matrix = new Matrix();
	
	//������
	private OnPatterChangeListener onPatterChangeListener;
	
	/*-------------------------------------------------------------------------*/
	
	
	/*-------------------------------���캯������---------------------------------*/
	//����3�����캯��
	public LockPatternView(Context context) {
		super(context);
	}
	
	public LockPatternView(Context context , AttributeSet attrs) {
		super(context , attrs);
	}
	
	public LockPatternView(Context context , AttributeSet attrs , int defStyleAttr) {
		super(context , attrs , defStyleAttr);
	}
	/*-------------------------------------------------------------------------*/
	
	
	
	
	//������9����
	@Override
	protected void onDraw(Canvas canvas) {
		//�������Ȱ���9�����ʼ��һ��
		if( ! isInit){ //���û�о�����ʼ��
			initPoints(); //�Ǿ͵������������ʼ��һ��
		}
		
		//����
		points2Canvas(canvas);
		
		//����
		if(pointList.size() > 0){
			Point a = pointList.get(0);
			//���ƾŹ�������ĵ�
			for(int i=0; i<pointList.size() ; i++){
				Point b = pointList.get(i);
				line2Canvas(canvas, a, b);
				a = b; //һ�λ�����֮��ԭ����b������µ�a�㣬���µ�b��������һ��ѭ���л��
			}
			//�������������
			if(movingNoPoint){
				line2Canvas( canvas , a , new Point(movingX , movingY) );
			}
		}
	}
	
	
	/*-------------------------------------------------------------------------*/

	//һ���Զ��庯�������ڵ�ĳ�ʼ��
	private void initPoints(){
		//(1)���ǵû�ȡ���ֵĿ�͸ߣ�֮����Ҫ����������Ϊ�������������λ���ǲ�һ���ģ���������Ӧ����ȷ�ϵ�ǰ״̬���Ǻ�������������
		width = getWidth();
		height = getHeight();
		
		//(2)��ƫ����
		//Ȼ���ж��Ǻ�����������
		if(width > height){ //����Ǻ������򡭡�
			offsetX = (width - height)/2;
			width = height; //���ھŹ����������ͣ���ʱheight��̣��Ǿ�����Ϊ��׼����width��height�ĳ���һ��
		}
		else{ //������������򡭡�
			offsetY = (height - width)/2;
			height = width; //���ھŹ����������ͣ���ʱwidth��̣��Ǿ�����Ϊ��׼����height��width�ĳ���һ��
		}
		
		
		//(3)����ͼƬ��Դ
		pointNormal  = BitmapFactory.decodeResource(getResources() , R.drawable.oval_normal);
		pointPressed  = BitmapFactory.decodeResource(getResources() , R.drawable.oval_pressed);
		pointError  = BitmapFactory.decodeResource(getResources() , R.drawable.oval_error);
		linePressed  = BitmapFactory.decodeResource(getResources() , R.drawable.line_pressed);
		lineError  = BitmapFactory.decodeResource(getResources() , R.drawable.line_error);
		
		//(4)���õ������
		//��һ��
		points[0][0] = new Point(offsetX + width/4 , offsetY + width/4);
		points[0][1] = new Point(offsetX + width/2 , offsetY + width/4);
		points[0][2] = new Point(offsetX + width - width/4, offsetY + width/4);
		//�ڶ���
		points[1][0] = new Point(offsetX + width/4 , offsetY + width/2);
		points[1][1] = new Point(offsetX + width/2 , offsetY + width/2);
		points[1][2] = new Point(offsetX + width - width/4 , offsetY + width/2);
		//������
		points[2][0] = new Point(offsetX + width/4 , offsetY + width - width/4);
		points[2][1] = new Point(offsetX + width/2 , offsetY + width - width/4);
		points[2][2] = new Point(offsetX + width - width/4 , offsetY + width - width/4);
		
		//(5)����ͼƬ��Դ�İ뾶���û�������׼ȷ
		bitmapR = pointNormal.getWidth() / 2; //�����ͻ�ȡ��ͼ���һ�룬Ҳ����Բ�İ뾶
		
		//(6)��������
		int index = 1;
		for(Point[] p : points){
			for(Point q : p){
				q.index = index;
				index ++ ;
			}
		}
		
		//(7)��ʼ����ɣ���isInit��־����Ϊtrue
		isInit = true;
		
	}//�Զ��庯��initPoints()����
	
	/*-------------------------------------------------------------------------*/
	
	//һ���Զ��庯�������ڽ����ڻ����ϻ�����
	private void points2Canvas(Canvas canvas) {
		
		for(int i = 0; i< points.length ; i++){
			for(int j = 0; j< points[i].length ; j++){
				Point point = points[i][j];
				if(point.state == Point.STATE_PRESSED){
					//����֮����Ҫ��ȥbitmapR����Ϊ����������Բ���������Բ��Ϊ���ģ��������ڻ���ʱ���Ǵ�����߿�ʼ��������������һ���뾶��������ƫ��
					canvas.drawBitmap(pointPressed , point.x - bitmapR , point.y - bitmapR , paint);
				}
				else if(point.state == Point.STATE_NORMAL){
					canvas.drawBitmap(pointNormal , point.x - bitmapR , point.y - bitmapR , paint);
				}
				else if(point.state == Point.STATE_ERROR){
					canvas.drawBitmap(pointError , point.x - bitmapR , point.y - bitmapR , paint);
				}
			}
		}
		
	}//�Զ��庯��points2Canvas()����
	
	
	/*-------------------------------------------------------------------------*/
	
	private void line2Canvas(Canvas canvas , Point a , Point b){
		
		//��������֮���ߵĳ���
		float lineLength = (float) Math.sqrt( Math.abs(a.x - b.x) * Math.abs(a.x - b.x) + Math.abs(a.y - b.y) * Math.abs(a.y - b.y) );
		float degrees = getDegrees( a , b );
		
		canvas.rotate(degrees , a.x , a.y);
		
		if(a.state == Point.STATE_PRESSED){
			matrix.setScale(lineLength / linePressed.getWidth() , 1); //����������ߣ�ͼƬ��Դ��һ��С���飬�����Ƕ�̬����������x�����ϻ�����޴����ţ���y�����ϲ���
			matrix.postTranslate(a.x - linePressed.getWidth() , a.y - linePressed.getHeight());
			canvas.drawBitmap(linePressed, matrix, paint);
		}
		else{
			matrix.setScale(lineLength / lineError.getWidth() , 1);
			matrix.postTranslate(a.x - lineError.getWidth() , a.y - lineError.getHeight());
			canvas.drawBitmap(lineError, matrix, paint);
		}
		
		//��ת����
		canvas.rotate(-degrees , a.x , a.y);
	}
	
	/*-------------------------------------------------------------------------*/
	
	private float getDegrees(Point a , Point b){
		float ax = a.x;
		float ay = a.y;
		float bx = b.x;
		float by = b.y;
		float degrees = 0;
		
		if(ax == bx){ //y����� 90�Ȼ�270��
			if(by > ay){ //��y����±� 90
				degrees = 90;
			}
			else if(by < ay){ //��y����ϱ� 270
				degrees = 270;
			}
		}
		else if(ay == by){ //y����� 0��180
			if(bx > ax){ //��y����±� 90
				degrees = 0;
			}
			else if(bx < ax){ //��y����ϱ� 270
				degrees = 180;
			}
		}
		else{
			if(bx > ax){ //��y����ұ� 270~90
				if(by > ay){ //��y����±� 0~90
					degrees = 0;
					degrees = degrees + switchDegrees(Math.abs(by - ay) , Math.abs(bx - ax));
				}
				else if(by < ay){ //��y����ϱ� 270~0
					degrees = 360;
					degrees = degrees - switchDegrees(Math.abs(by - ay) , Math.abs(bx - ax));
				}
			}
			else if(bx < ax){ //��y������ 90~270
				if(by > ay){ //��y����±� 180~270
					degrees = 90;
					degrees = degrees + switchDegrees(Math.abs(bx - ax) , Math.abs(by - ay));
				}
				else if(by < ay){ //��y����ϱ� 90~180
					degrees = 270;
					degrees = degrees - switchDegrees(Math.abs(bx - ax) , Math.abs(by - ay));
				}
			}
		}
		return degrees;
	}
	
	
	/*-------------------------------------------------------------------------*/
	
	
	private float switchDegrees(float x , float y){
		//����ת��Ϊ�Ƕ�
		return (float) Math.toDegrees(Math.atan2(x , y));
	}
	
	
	/*-------------------------------------------------------------------------*/
	
	//���������ʱ�򣬻ᷢ��ʲô�����߼�
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		movingNoPoint = false;
		isFinish = false;
		//�����������Բ��������ӽ���������ĳһ����Χ�ڵ�ʱ�򣬾Ϳ�ʼ����һϵ���ж���
		movingX = event.getX();
		movingY = event.getY();
		
		Point point = null;
		
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			//�����굱ǰ�����¡���ʱ�����κ�һ����ķ�Χ֮�ڣ�checkSelectedPoint()�᷵��null����ʲô����Ҳ��ִ��
			//�����굱ǰ�����¡���ʱ����ĳһ����ķ�Χ�ڣ��Ͱ�����㷵�ػ��������������󵽣����ص��������ĵ�һ����
			
			//���»���
			if(onPatterChangeListener != null){
				onPatterChangeListener.onPatterStart(true);
			}
			
			for(int i=0; i<pointList.size() ; i++){
				Point p = pointList.get(i);
				p.state = Point.STATE_NORMAL;
			}
			pointList.clear(); //�ڻ���֮ǰ�����
			point = checkSelectedPoint();
			if(point != null){ //���ж����Ƿ�ѡ����ĳ���㣬һ����ѡ����ĳ���㣬����Ļ��ƾ���ʽ��ʼ��
				isSelect = true; //��Ȼ��ʼ�ˣ��Ͱ�isSelect����Ϊtrue����ʾ����ʼ�ˡ������ı�ʾ
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if(isSelect){ //������ƶ���ʱ�򣬷���isSelect��true����ʾ��ǰ���Կ�ʼ�����ˣ��Ǿ���checkSelectedPoint()���һ�������������Щ�����ĵ�
				point = checkSelectedPoint();
				if(point == null){
					movingNoPoint = true;
				}
			}
			break;
		case MotionEvent.ACTION_UP: //һ�����̧���ʱ�򣬱�ʾѡ���Ĺ��̾ͽ����ˣ��Ǿ���isSelectΪfalse������isFinishΪtrue��
			isFinish = true;
			isSelect = false;
			break;
		default:
			break;
		}
		
		//���ѱ�ѡ�еģ���ѡ���ظ��ĵ���м��
		if( ! isFinish && isSelect && point != null){
			if(pointList.contains(point)){ //�����ǰѡ�еĵ��Ѿ���������
				movingNoPoint = true;
			}
			else{ //�������һ���µ�
				point.state = Point.STATE_PRESSED;
				pointList.add(point);
			}
		}
		
		//���ƽ���
		if(isFinish){
			if(pointList.size() == 1){ //���Ʋ�����
				resetPoint();
			}
			else if(pointList.size() > 0 && pointList.size() < POINT_SINZE){ //������ƴ���
				for(Point p : pointList){
					p.state = Point.STATE_ERROR;
				}
				if(onPatterChangeListener != null){
					onPatterChangeListener.onPatterChange(null);
				}
			}
			else{ //������Ƴɹ�
				if(onPatterChangeListener != null){
					String passwordStr = "";
					for(int i=0; i<pointList.size() ; i++){
						passwordStr = passwordStr + pointList.get(i).index; //�ѽǱ�һ��һ��ƴ�ӵ��ַ�����
					}
					if( ! TextUtils.isEmpty(passwordStr)){
						onPatterChangeListener.onPatterChange(passwordStr);
					}
				}
			}
		}
		
		postInvalidate(); //ÿ��ִ��onTouchEvent��Ҫ���������������Viewˢ��һ��
		
		return true;
	}
	
	/*-------------------------------------------------------------------------*/
	
	public void resetPoint(){
		for(int i=0; i<pointList.size() ; i++){
			Point p = pointList.get(i);
			p.state = Point.STATE_NORMAL;
		}
		pointList.clear();
	}
	
	/*-------------------------------------------------------------------------*/
	
	//�ú���������������������������Բ�ڣ��Ͱѵ�ǰ���Բ���������ԥ�������س�ȥ����ʾ����㱻ѡ���ˡ����Ҫ������forѭ���������е�����ж�
	private Point checkSelectedPoint(){
		
		for(int i=0;i<points.length;i++){
			for(int j=0;j<points[i].length;j++){
				Point point = points[i][j];
				if( (point.x - movingX)*(point.x - movingX) + (point.y - movingY)*(point.y - movingY) < bitmapR*bitmapR ){
					return point;
				}
			}
		}
		
		return null;
	}
	
	/*-------------------------------------------------------------------------*/
	
	/*
	 * ����ͼ�����������������������onTouch��ʱ�򱻴�����
	 */
	public static interface OnPatterChangeListener{
		/*
		 * ͼ���ı�
		 * @param passwordStr ͼ������
		 */
		void onPatterChange(String passwordStr);
		/*
		 * ͼ�����»���
		 * @param isStart �Ƿ����»���
		 */
		void onPatterStart(boolean isStart);
	}
	
	
	/*
	 * ����ͼ��������
	 * @param onPatterChangeListener
	 */
	public void setPatterChangeListener(OnPatterChangeListener onPatterChangeListener){
		if(onPatterChangeListener != null){
			this.onPatterChangeListener = onPatterChangeListener;
		}
	}
	

	/*-------------------------------�ڲ��ಿ��---------------------------------*/
	public static class Point{
		
		//����ʱ��ı�ʾ
		public static int STATE_NORMAL = 0;
		
		//ѡ��ʱ��ı�ʾ
		public static int STATE_PRESSED = 1;
		
		//����ʱ��ı�ʾ
		public static int STATE_ERROR = 2;
		
		public float x,y; //���x,y����
		public int index = 0;
		public int state = 0;
		
		public Point(){
			//ʲôҲ��д
		}
		
		public Point (float x , float y){
			this.x = x;
			this.y = y;
		}
		
	}//Point�����
	/*----------------------------------------------------------------------*/
}

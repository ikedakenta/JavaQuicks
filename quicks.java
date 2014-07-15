/******************************************************************************************************
「Javaクイックス開発SDK」1.4
製作／池田プロダクション
開発環境：Eclipse 3.1 
                & JDK 5.0.1 Released by SunMicrosystems Company.
対象環境：Windows98/Me/2000/NT/XP JAVA実行対応IE
必要最低環境：CPU     333MHz
              メモリ  64MB
推奨環境　　：CPU     1GHz
　　　　　　　メモリ　128MB
********************************************************************************************************/
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.Stack;
/* キーボード入力ができるアプレットKeyTestクラスの定義 */
public class quicks extends Applet implements KeyListener,Runnable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*アプレットクラスの継承及びにスレッドクラスのインタフェースのインプリメント
	キーイベント取得用リスナーインタフェースのインプリメント*/	
	
	//変数定義/////////////////////////////////////////////////////////////////////////////
	final int UP=5,DOWN=6,RIGHT=7,LEFT=8;//上下左右を簡単な書式で制御できるようにしておく
	int clear_menseki=85;//クリアー面積　何%切るとクリアーになるか
	public int FRAME_LATE=60;//フレームレート　秒間何度画面を再描画更新するか
	public AudioClip kiri_sound;//切り取り中の音
	public AudioClip kakomi_sound;//囲み完了音
	public AudioClip butukari_sound;//ぶつかったときの音
	public AudioClip gameover_sound;//ゲームオーバーのときの音
	public AudioClip stageclear_sound;//ステージクリアーのときの音
	public AudioClip start_sound;//スタート音
	public MediaTracker mt;//メディアトラッカー
	public Font default_font;//デフォルトフォント
	public int font=24;//フォントサイズ
	public String color ="";//フォントカラー
	public boolean gameover_flag=false;//ゲームオーバーかどうかのフラグ
	public boolean clear_gazou=false;//クリアー画像のスイッチ
	public String ss="KeyTyped: ";
	public boolean gameplay=true;
	public boolean clear=false;//クリアーフラグ
	public String gameover_massage="";//ゲームオーバー時メッセージ
	public String stage_clear_massage="";//ステージクリアーメッセージ
	public String gameclear_URL="";//ゲームクリアー時に飛ぶURL
	public boolean jump_URL=false;//ゲームクリアー時に指定URLへ飛ぶかどうか
	public int time_rimit=1000;//時間
	public int main_time=1000;
	public boolean time_hurry=false;//制限時間せまってきたときのフラグ
	public boolean time_infinity=false;//時間制限なしフラグ
	public int ct=0,i=0,i2=0;
	public int tempx_max=0,tempy_max=0,tempx_min=0,tempy_min=0;//囲み判定クラスで使うための大域変数
	public int magari=0;//移動用回数変数
	public enemy_manager enemy_thread = null;//敵スレッド
	public time_manager time_thread = null;//時間管理用クラス
	public int teki_kazu=3;//敵の数
	public Image backcg;//背景描画用
	public Image frontcg;//前面描画用
	public byte [][] map=new byte[640][480];//マップ
	public int [][] idou=new int[2000][2];//自分の移動軌跡[]回分の曲がり角を保存
	public double parsent=0;//何%切ったか
	public int kisuu=5;//自機ライフポイント
	public sibou kill= new sibou(this);//死亡クラス Quicksクラスのインスタンス(this)を参照渡し
	public kakomi_hantei kakomi = new kakomi_hantei(this);//囲み判定クラスにQuicksクラスのインスタンス(this)を参照渡し
	
	public player player1;//自機クラス
	public enemy teki [] = new enemy[7];//敵配列確保、インスタンスはまだ生成されてない
	public Image pri_surface=null;//前面描画用オブジェクト　プライマリサーフェス
	public Image back_surface=null;//後ろで画面処理を事前に済ませるのに使う
	public Image back_surface2=null;
	public Image back_surface3=null;
	public Graphics backbuf=null;//背景描画メモリ用（ダブルバッファリング)
	public Graphics backbuf2=null;//背景描画用メモリその2
	public Graphics backbuf3=null;//背景描画用メモリその3
	public Graphics backbuf4=null;//背景描画用メモリその4
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//アプレット終了時に実行されるメソッド///////////////////////////////////////////////////////////////
	public void destroy(){
		//描画領域解放
		backbuf.dispose();
		backbuf2.dispose();
		backbuf3.dispose();
		backbuf4.dispose();
		pri_surface.flush();
		back_surface.flush();
		back_surface2.flush();
		back_surface3.flush();

		//スレッド破棄
		enemy_thread.stop();
		time_thread.stop();
		//キーリスナーの切り離し
		removeKeyListener(this);
		//自身アプレットスレッドの停止
		this.stop();
	}
	
	//アプレット起動時に最初に実行されるメソッド/////////////////////////////////////////////////////////
	public void init(){
		Dimension d=getSize();//自動でアプレットのウィンドウサイズを取得
		pri_surface=createImage(d.width,d.height);//アプレットウィンドウサイズと同じ大きさの描画領域確保
		backbuf=pri_surface.getGraphics();
		back_surface=createImage(d.width,d.height);
		backbuf2=back_surface.getGraphics();
		back_surface2=createImage(d.width,d.height);
		backbuf3=back_surface2.getGraphics();
		back_surface3=createImage(d.width,d.height);
		backbuf4=back_surface3.getGraphics();

		addKeyListener(this);//キーリスナ登録
		requestFocus();//フォーカスを得る
		
		
		String s_init=null;
		Integer temp_init = new Integer(3);
		s_init=getParameter("teki_kazu");
		if (s_init != null){
			temp_init = Integer.valueOf(s_init);
			if(s_init.length()>2){//3桁以上の数字だったら小さくする
				temp_init=Integer.valueOf(7);
			}

			if(temp_init.intValue() > 7){//大きすぎると困るので安全処理
				temp_init=Integer.valueOf(7);
			}
			else if(temp_init.intValue()<1){
				temp_init=Integer.valueOf(1);
			}
		}
		teki_kazu=temp_init.intValue();
		
		player1=new player(1,1);//自機インスタンス　初期位置を与えて生成
		
		//敵インスタンス作成
		for (int i=0;i<teki_kazu;i++){
			teki[i] = new enemy(this);
		}

		clear=false;//クリアーフラグ
		gameover_flag=false;//ゲームオーバーかどうかのフラグ
		clear_gazou=false;
		ss="KeyTyped: ";
		ct=i=i2=0;
		tempx_max=tempy_max=tempx_min=tempy_min=0;
		parsent=0;//切り取り%　を０に初期化
		kakomi.par=0;//切り取り%を０に初期化
		
		mt = new MediaTracker(this);//メディアトラッカーの生成
		LoadImage();//画像読み込み関数を実行して全ての画像を読み込んでおく
		
		map_clear();//マップ情報の初期化
		Back();//描画処理バックメモリ領域の初期化
		
		player1.width=player1.gazou.getWidth(this);//自機画像の幅取得
		player1.height=player1.gazou.getHeight(this);//自機画像の高さ取得
		player1.kiri_width=player1.gazou_kiri.getWidth(this);//自機切り取り中画像の幅取得
		player1.kiri_height=player1.gazou_kiri.getHeight(this);//自機切り取り中画像の高さ取得
		
		for (int i=0;i<teki_kazu;i++){
			teki[i].width = teki[i].gazou.getWidth(this);//敵画像の幅取得
			teki[i].height=teki[i].gazou.getHeight(this);//敵画像の高さ取得
		}
		
		main_time=time_rimit;		
		time_thread=new time_manager(this);//時間管理用スレッド生成
		time_thread.start();
		
		enemy_thread = new enemy_manager(this);//敵管理スレッド生成
		enemy_thread.start();
	}
	
	//マップ情報を初期化//////////////////////////////////////////////////////////////////////////////////
	public void map_clear(){
		for(int x=0;x<640;x++){//マップ情報を全て０へ
			for(int y=0;y<480;y++){
				map[x][y]=0;
			}
		}
		for(int x2=1;x2<640;x2++){//上下左右の辺を移動可能領域にする　最初の自分の陣地代わり
			map[x2][1]=2;
			map[x2][477]=2;
		}
		for(int y2=1;y2<480;y2++){
			map[1][y2]=2;
			map[637][y2]=2;
		}
		for(int x2=0;x2<640;x2++){//上下左右の辺を囲い済み領域と同等の条件にする　壁代わり
			map[x2][0]=1;
			map[x2][479]=map[x2][478]=1;
		}
		for(int y2=0;y2<480;y2++){
			map[0][y2]=1;
			map[639][y2]=map[638][y2]=1;
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	//画像読み込み関数///////////////////////////////////////////////////////////////////////////////////////////////
	public void LoadImage(){
		String s=null;
		Integer temp = new Integer(0);

		s = getParameter("back");//背景画像 jpeg画像
		if (s == null) s = "CG/back.jpg";
		backcg = getImage(getDocumentBase(), s);
		mt.addImage(backcg,0);
		
		s = getParameter("front");//前面画像 jpeg画像
		if (s == null)s = "CG/front.jpg";
		frontcg = getImage(getDocumentBase(), s);
		mt.addImage(frontcg,0);
		s = getParameter("player1");//自機画像
		if (s == null) s = "CG/player1.gif";
		player1.gazou = getImage(getDocumentBase(), s);
		mt.addImage(player1.gazou,1);
		s = getParameter("player1_kiri");//自機画像
		if (s == null) s = "CG/player1_kiri.gif";
		player1.gazou_kiri = getImage(getDocumentBase(), s);
		mt.addImage(player1.gazou_kiri,1);
		
		for(int i=0;i<teki_kazu;i++){
			s = getParameter("teki" + String.valueOf(i));//敵画像 gif画像
			if (s == null) s = "CG/teki" + String.valueOf(i) + ".gif";
			teki[i].gazou = getImage(getDocumentBase(), s);
			mt.addImage(teki[i].gazou,1);
		}
		
		
		stage_clear_massage = getParameter("stage_clear_massage");//ステージクリアーメッセージ
		if (stage_clear_massage == null) stage_clear_massage = "ステージクリアー！！";
		gameover_massage = getParameter("gameover_massage");//ゲームオーバーメッセージ
		if (gameover_massage == null) gameover_massage = "GAME OVER";
		gameclear_URL = getParameter("gameclear_URL");//URLが書かれていたら
		if (gameclear_URL != null){
			jump_URL = true;//書かかれていたらURL自動ジャンプ機能のフラグをたてる
		}
		else if(gameclear_URL ==null){
			gameclear_URL = "./next_stage/index.html";//かかれていなかったらデフォルトのアドレスへ飛ぶ仕組み
			jump_URL = true;
		}
		if(gameclear_URL.equals("no")==true){
			jump_URL=false;//URLへ飛ばない
		}

	
		default_font = new Font("ＭＳ 明朝",Font.PLAIN,font);//デフォルトのサイズ
		
		s = getParameter("kiri");//各種音声読み込み
		if (s == null) s = "sound/kiri.wav";
		kiri_sound = getAudioClip(getDocumentBase(), s);
		s = getParameter("kakomi");//各種音声読み込み
		if (s == null) s = "sound/kakomi.wav";
		kakomi_sound = getAudioClip(getDocumentBase(), s);
		s = getParameter("butukari");//各種音声読み込み
		if (s == null) s = "sound/butukari.wav";
		butukari_sound = getAudioClip(getDocumentBase(), s);
		s = getParameter("gameover");//各種音声読み込み
		if (s == null) s = "sound/gameover.wav";
		gameover_sound = getAudioClip(getDocumentBase(), s);
		s = getParameter("stageclear");//各種音声読み込み
		if (s == null) s = "sound/stageclear.wav";
		stageclear_sound = getAudioClip(getDocumentBase(), s);
		s = getParameter("start");//各種音声読み込み
		if (s == null) s = "sound/start.wav";
		start_sound = getAudioClip(getDocumentBase(), s);

		s=getParameter("time");
		temp = Integer.valueOf(1000);//とりあえず設定しないといきなりtime_threadにより時間切れ→Gameover
		if (s != null){//時間制限
			temp = Integer.valueOf(s);
			if(s.length()>3){//4桁以上の数字だったら小さくする
				temp = Integer.valueOf(1000);
			}
			
			if(temp.intValue()==0){
				time_infinity=true;//時間制限なし
			}

		}
		else if(s==null){
			time_infinity=true;//時間制限なし
		}
		time_rimit=temp.intValue();
		main_time=temp.intValue();		
		
		FRAME_LATE = 30;
		s=getParameter("frame_late");
		if (s != null){//フレームレート
			temp = Integer.valueOf(s);
			if(s.length()>4){//4桁以上の数字だったら小さくする
				temp=Integer.valueOf(1000);
			}
			
			if(temp.intValue()>1000){//早すぎると困るので安全処理
				temp=Integer.valueOf(1000);
			}
			else if(temp.intValue()<1){
				temp=Integer.valueOf(1);
			}
			FRAME_LATE=(temp.intValue() + 5);//最低でも5
		}
		
		
		for (int i=0;i<teki_kazu;i++){
			s = getParameter("teki" + String.valueOf(i) + "_speed");
			if (s != null){
				temp = Integer.valueOf(s);
				if(s.length()>2){//4桁以上の数字だったら小さくする
					temp=Integer.valueOf(10);
				}

				if(temp.intValue()>10){//早すぎると困るので安全処理
					temp=Integer.valueOf(10);
				}
				else if(temp.intValue()<0){//マイナスはなし
					temp=Integer.valueOf(1);
				}
				teki[i].sokudo=temp.intValue();
			}
		}
		
		
		s=getParameter("parsent");
		if (s != null){
			//getParameter("parsent").valueOf(temp);//何%切るとクリアーになるか
			temp = Integer.valueOf(s);
			if(s.length()>3){//3桁以上の数字だったら小さくする
				temp=Integer.valueOf(100);
			}

			if(temp.intValue()>100){//大きすぎると困るので安全処理
				temp=Integer.valueOf(100);
			}
			else if(temp.intValue()<1){
				temp=Integer.valueOf(1);
			}
			clear_menseki=temp.intValue();
		}
		s=getParameter("kisuu");
		if (s != null){
			//getParameter("kisuu").valueOf(temp);//自機ライフポイント
			temp = Integer.valueOf(s);
			if(s.length()>3){//3桁以上の数字だったら小さくする
				temp=Integer.valueOf(99);
			}
			if(temp.intValue()>100){//大きすぎると困るので安全処理
				temp=Integer.valueOf(99);
			}
			else if(temp.intValue()<1){
				temp=Integer.valueOf(1);
			}
			kisuu=temp.intValue();
			player1.kisuu=temp.intValue();
		}
		
		s=getParameter("font");
		if (s != null){
			//getParameter("font").valueOf(temp);
			temp = Integer.valueOf(s);
			if(s.length()>3){//3桁以上の数字だったら小さくする
				temp=Integer.valueOf(72);
			}

			if(temp.intValue()>72){//大きすぎると困るので安全処理
				temp=Integer.valueOf(72);
			}
			else if(temp.intValue()<12){
				temp=Integer.valueOf(12);
			}
			font=temp.intValue();
		}
		
		s=getParameter("color");
		if (s != null){
			if(s.length()==7){//3桁以上の数字だったら小さくする
				color=s.substring(1);
				System.err.println(color); 
			}else{
				color=null;
			}
		}
		
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public void Back(){//描画処理バックメモリ領域の初期化---------------------------------------------------
		try {
			mt.waitForAll();//全ての画像を読み込むまで待機
		} catch (Exception e) {}
		
		this.backbuf2.drawImage(this.frontcg,0,0,null);									
		this.backbuf2.setColor(Color.white);//カラーを白にセット
		this.backbuf3.drawImage(this.frontcg,0,0,null);									
		this.backbuf3.setColor(Color.white);//カラーを白にセット
		for(i=1;i<639;i++){//隅の領域境界線(2の領域）は白に塗る
			if(map[i][1]==2){
				backbuf3.drawLine(i,1,i,1);
			}
			if(map[i][477]==2){
				backbuf3.drawLine(i,477,i,477);
			}
		}
		for(i=1;i<479;i++){
			if(map[1][i]==2){
				backbuf3.drawLine(1,i,1,i);
			}
			if(map[637][i]==2){
				backbuf3.drawLine(637,i,637,i);
			}
		}
		backbuf3.setColor(Color.blue);//カラーを青にセット
		backbuf3.drawRect(0,0,639,479);//画面一番端の列を青で線を引く
		backbuf3.drawRect(638,0,638,479);
		backbuf3.drawRect(0,478,639,478);
		//一番端っこの部分をあらためて白で塗りなおす。
		backbuf.setFont(default_font);
		backbuf.setColor(Color.white);//カラーを白にセット
		backbuf2.drawImage(back_surface2,0,0,null);
		
	}//-------------------------------------------------------------------------------------------------------
	
	
	//ゲームリスタート関数/////////////////////////////////////////////////////////////////////////////
	public void restart(){
		
		for(int i=0;i<teki_kazu;i++){//敵全部生き返らせる
			teki[i].dead = false;
		}
		
		clear_gazou=false;
		gameover_flag=false;//ゲームオーバーフラグを初期化
		if(time_infinity==false){//時間制限フラグがあれば
			main_time=time_rimit;//時間制限を代入
			time_hurry=false;
			time_thread.contine();
		}
		ss="KeyTyped: ";
		player1.kisuu=kisuu;
		player1.x=1;//プレイヤー1情報の初期化
		player1.y=1;
		player1.kiridasi=false;
		ct=i=i2=0;
		tempx_max=tempy_min=tempx_min=tempy_min=0;
		parsent=0;//切り取り%　を０に初期化
		kakomi.par=0;
		
		map_clear();//マップ情報の初期化
		//if (bgm_sound != null) bgm_sound.loop();// BGM音ﾙｰﾌﾟ再生
		start_sound.play();//スタート音を鳴らす
		Back();//描画処理バックメモリ領域の初期化
		gameplay=true;
		contine();
		
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	//文字コード検出///////////////////////////////////////////////////////////////////////////////////
	public void keyTyped(KeyEvent e){//キーが入力されたら
		String ss="";
		ss=ss+e.getKeyChar();

		
		if(ss.equals("r")==true){// 「R」を押すと
			restart();//ゲーム再起動
		}
		
		
		if(gameover_flag==true){//ゲームオーバーの時
			restart();//ゲームをリスタートする
		}
		else if(clear==true){//クリアーの時
			clear_gazou=true;//クリアーしたので画像を全部見れる
			if(jump_URL==true){//URL指定があるのであればそこへ飛ぶ
				try {
					this.getAppletContext().showDocument( new URL(getDocumentBase(), gameclear_URL ));
				} catch (Exception error) {
					System.err.println("URL JUMP Error !!! : "+ gameclear_URL );
				}
			}
			else{//URL指定がないとき
				repaint();//描画
			}
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	//仮想キーコード検出///////////////////////////////////////////////////////////////////////////////
	public void keyPressed(KeyEvent e){//キーが押されていたら
		if(gameover_flag==false && clear==false){//ゲームオーバー、ステージクリアーの時はここの処理はしない
			int cd=e.getKeyCode();
			boolean ctrl=false;
			if(magari>=2000){
				kill.dead();
				magari=0;//曲がり変数が999を超えたら（999回以上曲がったら）０に戻す
			}
			if((e.getModifiers() & InputEvent.CTRL_MASK)!= 0) {
				ctrl=true;//切り出し検知に使う
			}
			
			
			if(player1.kiridasi==false){//まだ切り出していなくて
				if(ctrl==true){//切り出しボタン押していたら切り出しを開始する
					magari=0;//初期化
					idou[magari][0]=player1.x;
					idou[magari][1]=player1.y;
					magari++;//一回曲がったことにする
				}
			}
			
			
			if(player1.kiridasi==true){//切り取り作業中//////////////////////////////////////////////////////////
				if(player1.muki==player1.LEFT){
					map[player1.x+1][player1.y]=3;//通った場所のマップを3へ
				}
				else if(player1.muki==player1.RIGHT){
					map[player1.x-1][player1.y]=3;//通った場所のマップを3へ
				}
				else if(player1.muki==player1.UP){
					map[player1.x][player1.y+1]=3;//通った場所のマップを3へ
				}
				else if(player1.muki==player1.DOWN){
					map[player1.x][player1.y-1]=3;//通った場所のマップを3へ
				}
				map[player1.x][player1.y]=3;//通った場所のマップを3へ
				kiri_sound.play();//切り取り音
			}////////////////////////////////////////////////////////////////////////////////////////////////////
			
			
			
			switch(cd){
			case KeyEvent.VK_LEFT:              // (自機移動left)
				if(player1.x==1){break;}
				if(player1.muki!=player1.LEFT){//向きが変わったら　曲がり角を記憶
					idou[magari][0]=player1.x;
					idou[magari][1]=player1.y;
					magari++;//一回曲がったことにする
				}
				
				
				if(ctrl==true){
					if(player1.kiridasi==false){
						if(map[player1.x-2][player1.y]==0){//マップにおいて未開拓領域であれば
							map[player1.x][player1.y]=3;//出発点のマップを3へ
							player1.kiridasi=true;
							if (player1.x >= 2) player1.x-=2;
							map[player1.x][player1.y]=3;//通った場所のマップを3へ
							
							player1.muki=player1.LEFT;//現在の向きを左へ設定
							player1.start_muki=player1.LEFT;//切り出し初期向きを左へ設定
						}
					}
					else if(player1.kiridasi==true){
						if(map[player1.x-2][player1.y]==0){//マップにおいて未開拓領域であれば
							if (player1.x >= 2) player1.x-=2;
							map[player1.x][player1.y]=3;//通った場所のマップを3へ
							player1.muki=player1.LEFT;
						}
						else if(map[player1.x-2][player1.y]==2){//切り取り　完了のとき
							if (player1.x >= 2) player1.x-=2;
							map[player1.x][player1.y]=map[player1.x+1][player1.y]=3;//通った場所のマップを3へ
							player1.muki=player1.LEFT;
							player1.end_muki=player1.LEFT;//切り出し終了向きを左へ設定
							idou[magari][0]=player1.x;
							idou[magari][1]=player1.y;
							magari++;//一回曲がったことにする
							kakomi.kakomi();
						}
					}
				}
				else{
					if(player1.kiridasi==false){
						if(map[player1.x-2][player1.y]==2){//マップにおいて移動可能領域であれば
							player1.x-=2;
							player1.muki=player1.LEFT;
						}
					}
					else if(player1.kiridasi==true){
						if(map[player1.x-2][player1.y]==0){//マップにおいて未開拓領域であれば
							map[player1.x][player1.y]=3;//通った場所のマップを3へ
							if (player1.x >= 2) player1.x-=2;
							player1.muki=player1.LEFT;
						}
						else if(map[player1.x-2][player1.y]==2){//切り取り　完了のとき
							if (player1.x >= 2) player1.x-=2;
							map[player1.x][player1.y]=map[player1.x+1][player1.y]=3;//通った場所のマップを3へ
							player1.muki=player1.LEFT;
							player1.end_muki=player1.LEFT;//切り出し終了向きを左へ設定
							idou[magari][0]=player1.x;
							idou[magari][1]=player1.y;
							magari++;//一回曲がったことにする
							
							kakomi.kakomi();
						}
					}
				}
				break;
			case KeyEvent.VK_RIGHT:				// (自機移動right)
				if(player1.x==637){break;}
				if(player1.muki!=player1.RIGHT){
					idou[magari][0]=player1.x;
					idou[magari][1]=player1.y;
					magari++;//一回曲がったことにする
				}

				if(ctrl==true){
					if(player1.kiridasi==false){
						if(map[player1.x+2][player1.y]==0){//マップにおいて未開拓領域であれば
							map[player1.x][player1.y]=3;//出発点のマップを3へ
							player1.kiridasi=true;
							if (player1.x <= 639) player1.x+=2;
							player1.muki=player1.RIGHT;
							player1.start_muki=player1.RIGHT;//切り出し開始向きを右へ設定
						}
					}
					else if(player1.kiridasi==true){
						if(map[player1.x+2][player1.y]==0){//マップにおいて未開拓領域であれば
							if (player1.x <= 639) player1.x+=2;
							player1.muki=player1.RIGHT;
						}
						else if(map[player1.x+2][player1.y]==2){//切り取り　完了のとき
							if (player1.x <= 639) player1.x+=2;
							map[player1.x][player1.y]=map[player1.x-1][player1.y]=3;//通った場所のマップを3へ
							player1.muki=player1.RIGHT;
							player1.end_muki=player1.RIGHT;//切り出し終了向きを右へ設定
							idou[magari][0]=player1.x;
							idou[magari][1]=player1.y;
							magari++;//一回曲がったことにする
							
							kakomi.kakomi();
						}
					}
				}
				else{
					if(player1.kiridasi==false){
						if(map[player1.x+2][player1.y]==2){//マップにおいて移動可能領域であれば
							player1.x+=2;
							player1.muki=player1.RIGHT;
						}
					}
					else if(player1.kiridasi==true){
						if(map[player1.x+2][player1.y]==0){//マップにおいて未開拓領域であれば
							if (player1.x <= 639) player1.x+=2;
							player1.muki=player1.RIGHT;
						}
						else if(map[player1.x+2][player1.y]==2){//切り取り　完了のとき
							if (player1.x <= 639) player1.x+=2;
							map[player1.x][player1.y]=map[player1.x-1][player1.y]=3;//通った場所のマップを3へ
							player1.muki=player1.RIGHT;
							player1.end_muki=player1.RIGHT;//切り出し終了向きを右へ設定
							idou[magari][0]=player1.x;
							idou[magari][1]=player1.y;
							magari++;//一回曲がったことにする
							
							kakomi.kakomi();
						}
					}
				}
				break;
			case KeyEvent.VK_UP:				// (自機移動up)
				if(player1.y==1){break;}
				if(player1.muki!=player1.UP){
					idou[magari][0]=player1.x;
					idou[magari][1]=player1.y;
					magari++;//一回曲がったことにする
				}

				if(ctrl==true){
					if(player1.kiridasi==false){
						if(map[player1.x][player1.y-2]==0){//マップにおいて未開拓領域であれば
							map[player1.x][player1.y]=3;//出発点のマップを3へ
							player1.kiridasi=true;
							if (player1.y >= 2) player1.y-=2;
							player1.muki=player1.UP;
							player1.start_muki=player1.UP;//切り出し開始向きを上へ設定
						}
					}
					else if(player1.kiridasi==true){
						if(map[player1.x][player1.y-2]==0){//マップにおいて未開拓領域であれば
							if (player1.y >= 2) player1.y-=2;
							player1.muki=player1.UP;
						}
						else if(map[player1.x][player1.y-2]==2){//切り取り　完了のとき
							if (player1.y >=2) player1.y-=2;
							map[player1.x][player1.y]=map[player1.x][player1.y+1]=3;//通った場所のマップを3へ
							player1.muki=player1.UP;
							player1.end_muki=player1.UP;//切り出し終了向きを上へ設定
							idou[magari][0]=player1.x;
							idou[magari][1]=player1.y;
							magari++;//一回曲がったことにする
							
							kakomi.kakomi();
						}
					}
				}
				else{
					if(player1.kiridasi==false){
						if(map[player1.x][player1.y-2]==2){//マップにおいて移動可能領域であれば
							player1.y-=2;
							player1.muki=player1.UP;
						}
					}
					else if(player1.kiridasi==true){
						if(map[player1.x][player1.y-2]==0){//マップにおいて未開拓領域であれば
							if (player1.y >= 2) player1.y-=2;
							player1.muki=player1.UP;
						}
						else if(map[player1.x][player1.y-2]==2){//切り取り　完了のとき
							if (player1.y >=2) player1.y-=2;
							map[player1.x][player1.y]=map[player1.x][player1.y+1]=3;//通った場所のマップを3へ
							player1.muki=player1.UP;
							player1.end_muki=player1.UP;//切り出し終了向きを上へ設定
							idou[magari][0]=player1.x;
							idou[magari][1]=player1.y;
							magari++;//一回曲がったことにする
							
							kakomi.kakomi();
						}
					}
				}
				break;
			case KeyEvent.VK_DOWN:				// (自機移動down)
				if(player1.x==437){break;}
				if(player1.muki!=player1.DOWN){
					idou[magari][0]=player1.x;
					idou[magari][1]=player1.y;
					magari++;//一回曲がったことにする
				}

				if(ctrl==true){
					if(player1.kiridasi==false){
						if(map[player1.x][player1.y+2]==0){//マップにおいて未開拓領域であれば
							map[player1.x][player1.y]=3;//出発点のマップを3へ
							player1.kiridasi=true;
							if (player1.y <= 479) player1.y+=2;
							player1.muki=player1.DOWN;
							player1.start_muki=player1.DOWN;//切り出し開始向きを下へ設定
						}
					}
					else if(player1.kiridasi==true){
						if(map[player1.x][player1.y+2]==0){//マップにおいて未開拓領域であれば
							if (player1.y <= 479) player1.y+=2;
							player1.muki=player1.DOWN;
						}
						else if(map[player1.x][player1.y+2]==2){//切り取り　完了のとき
							if (player1.y <= 479) player1.y+=2;
							map[player1.x][player1.y]=map[player1.x][player1.y-1]=3;//通った場所のマップを3へ
							player1.muki=player1.DOWN;
							player1.end_muki=player1.DOWN;//切り出し終了向きを下へ設定
							idou[magari][0]=player1.x;
							idou[magari][1]=player1.y;
							magari++;//一回曲がったことにする
							
							kakomi.kakomi();
						}
					}
				}
				else{
					if(player1.kiridasi==false){
						if(map[player1.x][player1.y+2]==2){//マップにおいて移動可能領域であれば
							player1.y+=2;
							player1.muki=player1.DOWN;
						}
					}
					else if(player1.kiridasi==true){
						if(map[player1.x][player1.y+2]==0){//マップにおいて未開拓領域であれば
							if (player1.y <= 479) player1.y+=2;
							player1.muki=player1.DOWN;
						}
						else if(map[player1.x][player1.y+2]==2){//切り取り　完了のとき
							if (player1.y <= 479) player1.y+=2;
							map[player1.x][player1.y]=map[player1.x][player1.y-1]=3;//通った場所のマップを3へ
							player1.muki=player1.DOWN;
							player1.end_muki=player1.DOWN;//切り出し終了向きを下へ設定
							idou[magari][0]=player1.x;
							idou[magari][1]=player1.y;
							magari++;//一回曲がったことにする
							
							kakomi.kakomi();
						}
					}
				}
				break;
			}
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////
	//キーを離した/////////////////////////////////////////////////////////////////////////////////////
	public void keyReleased(KeyEvent e){//キーが離されたら
		;//処理なし
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	//メイン処理ループ関数///////////////////////////////////////////////////////////////////////////
	public void run(){
		start_sound.play();//スタート音を鳴らす
			
		while (true){//無限ループ
			try{
				
				repaint();//描画
				Thread.sleep(1000/FRAME_LATE);//処理遅延時間　つまり、フレームレートの設定。
				
			}
			catch(Exception e){}//適当にエラー検知
		}
		
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void pause(){//スレッド停止を外部から呼び出すため
		enemy_thread.pause();
	}
	public void contine(){//スレッド再開を外部から指示するため
		enemy_thread.contine();
	}
	
	
	//ちらつき防止のため描画アップデート関数をオーバーロードして描画クリアー処理をはぶいている/////////
	public void update(Graphics g){
		paint(g);//描画
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////
	//************************************************************************************************
	
	public void gameover(){//ゲームオーバー
			if(gameover_flag==false){
				gameover_sound.play();//ゲームオーバーの音を鳴らす
				gameover_flag=true;//ゲームオーバーフラグON
			}
			backbuf.drawString(gameover_massage,32,200);
			backbuf.drawString("何かボタンを押してください",32,280+font);
			backbuf.drawString("リスタートします。",32,280+font+font);
			gameplay=false;
			time_thread.pause();
			enemy_thread.pause();
	}
	
	public void gameclear(){//ゲームクリアー
		if(clear_gazou==false){
				if(clear==false){
					clear=true;//クリアーフラグON
					stageclear_sound.play();//ステージクリアーの音を鳴らす
				}
				
				for (int i=0;i<teki_kazu;i++){
					teki[i].dead = true;
				}
					
				backbuf.drawString(stage_clear_massage,200,200);
				backbuf.drawString("何かボタンを押してください。",200,200+font);
			}
			else if(clear_gazou==true){
				backbuf.drawImage(backcg,0,0,null);//背景画像の表示
			}
			gameplay=false;
			if(time_infinity==false){
				time_thread.pause();//時間停止
			}
			enemy_thread.pause();//敵スレッドも停止
	}
	
	//画面描画関数//////////////////////////////////////////////////////////////////////////////////////
	public void paint(Graphics g){
		backbuf.drawImage(back_surface2,0,0,this);
		if(player1.kiridasi==true){
			backbuf.setColor(Color.green);//カラーを緑にセット
			backbuf.drawImage(player1.gazou_kiri,(player1.x-((int)player1.kiri_width/2)),(player1.y-((int)player1.kiri_height/2)),this);//自機の表示
			for(i=0;i<magari-1;i++){//切り取り線の表示
				backbuf.drawLine(idou[i][0],idou[i][1],idou[i+1][0],idou[i+1][1]);
			}
			backbuf.drawLine(idou[magari-1][0],idou[magari-1][1],player1.x,player1.y);//最後の曲がり角から現在地まで線引き
		}
		else if(player1.kiridasi==false){
			backbuf.drawImage(player1.gazou,(player1.x-((player1.width)/2)),(player1.y-((player1.height)/2)),this);//自機の表示
		}
		
		for(int i=0;i<teki_kazu;i++){
			if(teki[i].dead==false){
				backbuf.drawImage(teki[i].gazou,teki[i].x,teki[i].y,this);//敵の表示
			}
		}
		
		backbuf.setColor(Color.white);//カラーを白にセット
		if(color!=null){
			try{
				backbuf.setColor(new Color( Integer.decode("0x" + color.substring(0,2)).intValue(),
											Integer.decode("0x" + color.substring(2,4)).intValue(),
											Integer.decode("0x" + color.substring(4,6)).intValue() ));
			}catch(Exception e){
			}
		}
		if(time_infinity==false){//時間制限フラグあり
			if(time_hurry==true){
				backbuf.setColor(Color.red);
			}
			backbuf.drawString("自機残量：" + player1.kisuu + "　　開拓領域：" + Math.round(parsent)
							   + "%　　制限時間：" + main_time  +  "秒",10,475);//残機、開拓領域、時間の表示
		}
		else if(time_infinity==true){//時間制限フラグなし
			backbuf.drawString("自機残量：" + player1.kisuu + "　　開拓領域：" + Math.round(parsent)
							   + "%",10,475);//残機、開拓領域
		}	
		
		
		if(main_time<0 || player1.kisuu<=0){//ゲームオーバーならば
			gameover();
		}
		
		if(Math.round(parsent)>=clear_menseki){//ゲームクリアーならば
			gameclear();
		}
		
		g.drawImage(pri_surface,0,0,this);//裏のメモリ上画像を　表(本当の画面）へ転送
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////
}

//時間管理関数//////////////////////////////////////////////////////////////////////////////////////
class time_manager implements Runnable{
	public quicks quicks = null;
	private volatile Thread blinker;//スレッド処理用
	private boolean threadsuspended = false;//サスペンド用

	public time_manager(quicks q){//コンストラクタ　Quicksクラスからの参照渡し
		this.quicks = q;
	}
	public void start(){//外部からmyth.start()を呼び出し、スレッドを起動できる
		blinker = new Thread(this);
        blinker.start();
	}
	public void stop(){
		//blinker = null;
        //notify();
		blinker.stop();//スレッド破棄
	}
	public void contine(){//再開
        threadsuspended = false;
        //notify();
		//myth.contine();
	}
	public void pause(){//停止
		threadsuspended = true;
		//myth.pause();
	}
	
	public void setPriority(int obj){
		blinker.setPriority(obj);
	}
	
	public boolean isAlive(){
		if(blinker.isAlive()==true){//生きていたら
			//myth.destroy();//破棄
			return true;
		}
		return false;
	}
	
	public void run(){
		Thread thisThread = Thread.currentThread();
		while(blinker == thisThread){
			try{
                    while (threadsuspended)
                        wait();
                
				if(this.quicks.gameover_flag==false && this.quicks.clear_gazou==false){
					if(this.quicks.time_hurry==false){//時間制限そろそろやヴぁいフラグがまだ立ってない状態で
						if(this.quicks.main_time<60){//制限時間が残り60秒を切ったら
							this.quicks.time_hurry=true;//時間制限そろそろやヴぁいフラグを立てる
						}
					}
					this.quicks.main_time--;//時間を一秒減らす
				}
				Thread.sleep(1000);//一秒待つ
			}
			catch(Exception e){}//適当にエラー検知
		}
	}
}
	////////////////////////////////////////////////////////////////////////////////////////////////////

class enemy_manager implements Runnable{
	public quicks quicks = null;//Quicksクラスのインスタンスを参照渡しするために使う
	private volatile Thread blinker;//スレッド処理用
	private boolean threadsuspended = false;//サスペンド用

	public enemy_manager(quicks q){
		this.quicks = q;//Quicksクラスからのインスタンスを参照渡し
	}
	public void start(){//外部からmyth.start()を呼び出し、スレッドを起動できる
		blinker = new Thread(this);
        blinker.start();
	}
	public void stop(){
		//blinker = null;
        //notify();
		blinker.stop();//スレッド破棄
	}
	public void contine(){//再開
		threadsuspended = false;
        //notify();
		//myth.contine();
	}
	public void pause(){//停止
		threadsuspended = true;
		//myth.pause();
	}	
	public void setPriority(int obj){
		blinker.setPriority(obj);
	}
	
	public boolean isAlive(){
		if(blinker.isAlive()==true){//生きていたら
			//myth.destroy();//破棄
			return true;
		}
		return false;
	}
	
	public void run(){
		Thread thisThread = Thread.currentThread();
		while(blinker == thisThread){
			try{
                    while (threadsuspended){
                        wait();
                    }
                    
                    for (int i=0;i<quicks.teki_kazu;i++){
                    	quicks.teki[i].run();
                    }
					
					Thread.sleep(1000/this.quicks.FRAME_LATE);//処理遅延時間　つまり、フレームレートの設定。
			}
			catch(Exception e){}//適当にエラー検知
		}
	}
}
	////////////////////////////////////////////////////////////////////////////////////////////////////



//囲み判定処理クラス/////////////////////////////////////////////////////////////////////////////////
class kakomi_hantei{
	public quicks quicks= null;//Quicksクラスからのインスタンスを参照渡し
	final int UP=5,DOWN=6,RIGHT=7,LEFT=8;//上下左右を簡単な書式で制御できるようにしておく
	int i,i2,j;
	double par;//カウンタ、切った％の保持
	int a,b,c,d;
	int temp_min_x=637,temp_min_y=477;
	int temp_max_x=1,temp_max_y=1;
	
	int cheakmax_y=0;
	int cheakmax_x=0;
	int cheakmin_x=0;
	int cheakmin_y=0;
	int po1=0;
	int po2=0;
	int sarchmuki1=0,sarchmuki2=0;
	int sarchar1_x=1;
	int sarchar1_y=1;
	int sarchar2_x=1;
	int sarchar2_y=1;
	int cheak1[][] = new int[10000][2];
	int cheak2[][] = new int[10000][2];
	int turn1 = 0;
	int turn2 = 0;
	int nagasa1 = 0;
	int nagasa2 = 0;
	int startmuki;
	int endmuki;
	boolean flag1=false;
	int fillrectflag_x;
	int fillrectflag_y;
	int rectNo;
	int linepart_x=0;//分割した四角形の横の数
	int linepart_y=0;//分割した四角形の縦の数
	int x_num=0,y_num=0;//x_line,y_line配列の中身をカウントするため
	int x_line[] = new int[30000];//切り取り線のx座標を入れる
	int y_line[] = new int[30000];//切り取り線のy座標を入れる
	int temp_line[][] = new int[50000][2];
	int magattaKazu = 0;
	int magattaKazu1 = 0;
	int firstflag=0;
	boolean hantei=false;
	Stack stack = new Stack();
	int fillrectflag[] = new int[100000];
	int rect_num;
	
	byte kiri_num=3;//切った回数
	int point;//切り返した点の数
	
	public kakomi_hantei(quicks q){
		this.quicks = q;
	}
	
	public void kakomi(){
		
		if(this.quicks.time_infinity==false){//タイムスレッド一時停止
			quicks.time_thread.pause();
		}
		quicks.enemy_thread.pause();
		
		a=quicks.magari;
		for(i=0;i<a;i++){
			if(temp_max_x<quicks.idou[i][0]){//x座標の最大値を求める
				temp_max_x=quicks.idou[i][0];
			}
			if(temp_max_y<quicks.idou[i][1]){//y座標の最大値を求める
				temp_max_y=quicks.idou[i][1];
			}
			if(temp_min_x>quicks.idou[i][0]){//x座標の最小値を求める
				temp_min_x=quicks.idou[i][0];
			}
			if(temp_min_y>quicks.idou[i][1]){//y座標の最小値を求める
				temp_min_y=quicks.idou[i][1];
			}
		}

		//切り出し終了点の保持
		sarchar1_x=quicks.idou[quicks.magari-1][0];
		sarchar1_y=quicks.idou[quicks.magari-1][1];
		sarchar2_x=quicks.idou[quicks.magari-1][0];
		sarchar2_y=quicks.idou[quicks.magari-1][1];		
		
		//囲み関数が発動した時点での主人公の向き
		sarchmuki2=sarchmuki1=quicks.player1.end_muki;
		nagasa1=nagasa2=0;
		
		//囲むべき多角形の全角点を求める

		
		if(sarchmuki1==UP){
			if(quicks.map[sarchar1_x][sarchar1_y-1]==2){
				sarchmuki1=LEFT;
				sarchmuki2=RIGHT;
			}
		}else if(sarchmuki1==DOWN){
			if(quicks.map[sarchar1_x][sarchar1_y+1]==2){
				sarchmuki1=LEFT;
				sarchmuki2=RIGHT;
			}
		}else if(sarchmuki1==LEFT){
			if(quicks.map[sarchar1_x-1][sarchar1_y]==2){
				sarchmuki1=UP;
				sarchmuki2=DOWN;
			}
		}else if(sarchmuki1==RIGHT){
			if(quicks.map[sarchar1_x+1][sarchar1_y]==2){
				sarchmuki1=UP;
				sarchmuki2=DOWN;
			}
		}
		cheak1[0][0]=sarchar1_x;
		cheak1[0][1]=sarchar1_y;
		//---------------------
		
		po1=0;
		while(quicks.idou[0][0]!=cheak1[po1][0]||quicks.idou[0][1]!=cheak1[po1][1]){
			if(sarchmuki1==UP){
				for(;;){
					if(quicks.idou[0][0]==sarchar1_x&&quicks.idou[0][1]==sarchar1_y){
						break;
					}else if(quicks.map[sarchar1_x][sarchar1_y-1]==2){//まず上を探査
						nagasa1++;
						sarchar1_y=sarchar1_y-1;
					}else if(quicks.map[sarchar1_x-1][sarchar1_y]==2){//続いて左
						sarchmuki1=LEFT;
						break;
					}else if(quicks.map[sarchar1_x+1][sarchar1_y]==2){//それでもなければ今度は右
						sarchmuki1=RIGHT;
						break;
					}else if(quicks.idou[0][0]==sarchar1_x&&quicks.idou[0][1]==sarchar1_y-1){//それでもないとすると、すでに到着してるかもしれないのでチェック
						sarchar1_y=sarchar1_y-1;
						nagasa1++;
						break;
					}else if(quicks.idou[0][0]==sarchar1_x-1&&quicks.idou[0][1]==sarchar1_y){
						sarchar1_x-=1;
						sarchmuki1=LEFT;
						break;
					}else if(quicks.idou[0][0]==sarchar1_x+1&&quicks.idou[0][1]==sarchar1_y){
						sarchar1_x+=1;
						sarchmuki1=RIGHT;
						break;
					}
				}
			}else if(sarchmuki1==DOWN){
				for(;;){
					if(quicks.idou[0][0]==sarchar1_x&&quicks.idou[0][1]==sarchar1_y){
						break;
					}else if(quicks.map[sarchar1_x][sarchar1_y+1]==2){
						sarchar1_y=sarchar1_y+1;
						nagasa1++;
					}else if(quicks.map[sarchar1_x-1][sarchar1_y]==2){
						sarchmuki1=LEFT;
						break;
					}else if(quicks.map[sarchar1_x+1][sarchar1_y]==2){
						sarchmuki1=RIGHT;
						break;
					}else if(quicks.idou[0][0]==sarchar1_x&&quicks.idou[0][1]==sarchar1_y+1){
						sarchar1_y=sarchar1_y+1;
						nagasa1++;
						break;
					}else if(quicks.idou[0][0]==sarchar1_x-1&&quicks.idou[0][1]==sarchar1_y){
						sarchar1_x-=1;
						sarchmuki1=LEFT;
						break;
					}else if(quicks.idou[0][0]==sarchar1_x+1&&quicks.idou[0][1]==sarchar1_y){
						sarchar1_x+=1;
						sarchmuki1=RIGHT;
						break;
					}
				}
			}else if(sarchmuki1==LEFT){
				for(;;){
					if(quicks.idou[0][0]==sarchar1_x&&quicks.idou[0][1]==sarchar1_y){
						break;
					}else if(quicks.map[sarchar1_x-1][sarchar1_y]==2){
						sarchar1_x=sarchar1_x-1;
						nagasa1++;
					}else if(quicks.map[sarchar1_x][sarchar1_y-1]==2){
						sarchmuki1=UP;
						break;
					}else if(quicks.map[sarchar1_x][sarchar1_y+1]==2){
						sarchmuki1=DOWN;
						break;
					}else if((quicks.idou[0][0]==sarchar1_x-1) && (quicks.idou[0][1]==sarchar1_y) ){
						sarchar1_x=sarchar1_x-1;
						nagasa1++;
						break;
					}else if(quicks.idou[0][0]==sarchar1_x&&quicks.idou[0][1]==sarchar1_y-1){
						sarchar1_y-=1;
						sarchmuki1=UP;
						break;
					}else if(quicks.idou[0][0]==sarchar1_x&&quicks.idou[0][1]==sarchar1_y+1){
						sarchar1_y+=1;
						sarchmuki1=DOWN;
						break;
					}
				}
			}else if(sarchmuki1==RIGHT){
				for(;;){
					if(quicks.idou[0][0]==sarchar1_x&&quicks.idou[0][1]==sarchar1_y){
						break;
					}else if(quicks.map[sarchar1_x+1][sarchar1_y]==2){
						sarchar1_x=sarchar1_x+1;
						nagasa1++;
					}else if(quicks.map[sarchar1_x][sarchar1_y-1]==2){
						sarchmuki1=UP;
						break;
					}else if(quicks.map[sarchar1_x][sarchar1_y+1]==2){
						sarchmuki1=DOWN;
						break;
					}else if((quicks.idou[0][0]==sarchar1_x+1)&&(quicks.idou[0][1]==sarchar1_y)){
						sarchar1_x=sarchar1_x+1;
						nagasa1++;
						break;
					}else if(quicks.idou[0][0]==sarchar1_x&&quicks.idou[0][1]==sarchar1_y-1){
						sarchar1_y-=1;
						sarchmuki1=UP;
						break;
					}else if(quicks.idou[0][0]==sarchar1_x&&quicks.idou[0][1]==sarchar1_y+1){
						sarchar1_y+=1;
						sarchmuki1=DOWN;
						break;
					}
				}
			}
			po1++;
			cheak1[po1][0]=sarchar1_x;
			cheak1[po1][1]=sarchar1_y;
		}
		cheak1[0][0]=quicks.player1.x;
		cheak1[0][1]=quicks.player1.y;
		
		
		
		
		po2=0;
		while(quicks.idou[0][0]!=cheak2[po2][0]||quicks.idou[0][1]!=cheak2[po2][1]){
			if(sarchmuki2==UP){
				for(;;){
					if(quicks.idou[0][0]==sarchar2_x&&quicks.idou[0][1]==sarchar2_y){
						break;
					}else if(quicks.map[sarchar2_x][sarchar2_y-1]==2){
						sarchar2_y=sarchar2_y-1;
						nagasa2++;
					}else if(quicks.map[sarchar2_x+1][sarchar2_y]==2){
						sarchmuki2=RIGHT;
						break;
					}else if(quicks.map[sarchar2_x-1][sarchar2_y]==2){
						sarchmuki2=LEFT;
						break;
					}else if(quicks.idou[0][0]==sarchar2_x&&quicks.idou[0][1]==sarchar2_y-1){
						sarchar2_y=sarchar2_y-1;
						nagasa2++;
						break;
					}else if(quicks.idou[0][0]==sarchar2_x-1&&quicks.idou[0][1]==sarchar2_y){
						sarchar2_x-=1;
						sarchmuki2=LEFT;
						break;
					}else if(quicks.idou[0][0]==sarchar2_x+1&&quicks.idou[0][1]==sarchar2_y){
						sarchar2_x+=1;
						sarchmuki2=RIGHT;
						break;
					}
				}
			}else if(sarchmuki2==DOWN){
				for(;;){
					if(quicks.idou[0][0]==sarchar2_x&&quicks.idou[0][1]==sarchar2_y){
						break;
					}else if(quicks.map[sarchar2_x][sarchar2_y+1]==2){
						sarchar2_y=sarchar2_y+1;
						nagasa2++;
					}else if(quicks.map[sarchar2_x+1][sarchar2_y]==2){
						sarchmuki2=RIGHT;
						break;
					}else if(quicks.map[sarchar2_x-1][sarchar2_y]==2){
						sarchmuki2=LEFT;
						break;
					}else if(quicks.idou[0][0]==sarchar2_x&&quicks.idou[0][1]==sarchar2_y+1){
						sarchar2_y=sarchar2_y+1;
						nagasa2++;
						break;
					}else if(quicks.idou[0][0]==sarchar2_x-1&&quicks.idou[0][1]==sarchar2_y){
						sarchar2_x-=1;
						sarchmuki2=LEFT;
						break;
					}else if(quicks.idou[0][0]==sarchar2_x+1&&quicks.idou[0][1]==sarchar2_y){
						sarchar2_x+=1;
						sarchmuki2=RIGHT;
						break;
					}
				}
			}else if(sarchmuki2==LEFT){
				for(;;){
					if(quicks.idou[0][0]==sarchar2_x&&quicks.idou[0][1]==sarchar2_y){
						break;
					}else if(quicks.map[sarchar2_x-1][sarchar2_y]==2){
						sarchar2_x=sarchar2_x-1;
						nagasa2++;
					}else if(quicks.map[sarchar2_x][sarchar2_y+1]==2){
						sarchmuki2=DOWN;
						break;
					}else if(quicks.map[sarchar2_x][sarchar2_y-1]==2){
						sarchmuki2=UP;
						break;
					}else if((quicks.idou[0][0]==sarchar2_x-1) && (quicks.idou[0][1]==sarchar2_y) ){
						sarchar2_x=sarchar2_x-1;
						nagasa2++;
						break;
					}else if(quicks.idou[0][0]==sarchar2_x&&quicks.idou[0][1]==sarchar2_y-1){
						sarchar2_y-=1;
						sarchmuki2=UP;
						break;
					}else if(quicks.idou[0][0]==sarchar2_x&&quicks.idou[0][1]==sarchar2_y+1){
						sarchar2_y+=1;
						sarchmuki2=DOWN;
						break;
					}
				}
			}else if(sarchmuki2==RIGHT){
				for(;;){
					if(quicks.idou[0][0]==sarchar2_x&&quicks.idou[0][1]==sarchar2_y){
						break;
					}else if(quicks.map[sarchar2_x+1][sarchar2_y]==2){
						sarchar2_x=sarchar2_x+1;
						nagasa2++;
					}else if(quicks.map[sarchar2_x][sarchar2_y+1]==2){
						sarchmuki2=DOWN;
						break;
					}else if(quicks.map[sarchar2_x][sarchar2_y-1]==2){
						sarchmuki2=UP;
						break;
					}else if(quicks.idou[0][0]==sarchar2_x+1&&quicks.idou[0][1]==sarchar2_y){
						sarchar2_x=sarchar2_x+1;
						nagasa2++;
						break;
					}else if(quicks.idou[0][0]==sarchar2_x&&quicks.idou[0][1]==sarchar2_y-1){
						sarchar2_y-=1;
						sarchmuki2=UP;
						break;
					}else if(quicks.idou[0][0]==sarchar2_x&&quicks.idou[0][1]==sarchar2_y+1){
						sarchar2_y+=1;
						sarchmuki2=DOWN;
						break;
					}
				}
			}
			po2++;
			cheak2[po2][0]=sarchar2_x;
			cheak2[po2][1]=sarchar2_y;
		}
		cheak2[0][0]=quicks.player1.x;
		cheak2[0][1]=quicks.player1.y;
		
		
		
		
		
		
		if(nagasa1<nagasa2){//二つの探査方向でより距離が短かったほうを取る。
			flag1=true;
		}else{
			flag1=false;
		}
		
		cheakmax_x=temp_max_x;
		cheakmax_y=temp_max_y;
		cheakmin_x=temp_min_x;
		cheakmin_y=temp_min_y;
		
		//開始と終了の方向を求める。
		startmuki=quicks.player1.start_muki;
		endmuki=quicks.player1.end_muki;
		//ラベリングするための初期フラグ（"1"のこと）を１つ立てる
		if(startmuki==UP){
			if(endmuki==UP){
				if(flag1==true){
					fillrectflag_x=quicks.idou[0][0]-1;
					fillrectflag_y=quicks.idou[0][1]-1;
				}else{
					fillrectflag_x=quicks.idou[0][0]+1;
					fillrectflag_y=quicks.idou[0][1]-1;
				}
			}else if(endmuki==DOWN){
				if(flag1==true){
					fillrectflag_x=quicks.idou[0][0]+1;
					fillrectflag_y=quicks.idou[0][1]-1;
				}else{
					fillrectflag_x=quicks.idou[0][0]-1;
					fillrectflag_y=quicks.idou[0][1]-1;
				}
			}else if(endmuki==LEFT){
				if(flag1==true){
					fillrectflag_x=quicks.idou[0][0]+1;
					fillrectflag_y=quicks.idou[0][1]-1;
				}else{
					fillrectflag_x=quicks.idou[0][0]-1;
					fillrectflag_y=quicks.idou[0][1]-1;
				}
			}else if(endmuki==RIGHT){
				if(flag1==true){
					fillrectflag_x=quicks.idou[0][0]-1;
					fillrectflag_y=quicks.idou[0][1]-1;
				}else{
					fillrectflag_x=quicks.idou[0][0]+1;
					fillrectflag_y=quicks.idou[0][1]-1;
				}
			}
		}else if(startmuki==DOWN){
			if(endmuki==UP){
				if(flag1==true){
					fillrectflag_x=quicks.idou[0][0]+1;
					fillrectflag_y=quicks.idou[0][1]+1;
				}else{
					fillrectflag_x=quicks.idou[0][0]-1;
					fillrectflag_y=quicks.idou[0][1]+1;
				}
			}else if(endmuki==DOWN){
				if(flag1==true){
					fillrectflag_x=quicks.idou[0][0]-1;
					fillrectflag_y=quicks.idou[0][1]+1;
				}else{
					fillrectflag_x=quicks.idou[0][0]+1;
					fillrectflag_y=quicks.idou[0][1]+1;
				}
			}else if(endmuki==LEFT){
				if(flag1==true){
					fillrectflag_x=quicks.idou[0][0]-1;
					fillrectflag_y=quicks.idou[0][1]+1;
				}else{
					fillrectflag_x=quicks.idou[0][0]+1;
					fillrectflag_y=quicks.idou[0][1]+1;
				}
			}else if(endmuki==RIGHT){
				if(flag1==true){
					fillrectflag_x=quicks.idou[0][0]+1;
					fillrectflag_y=quicks.idou[0][1]+1;
				}else{
					fillrectflag_x=quicks.idou[0][0]-1;
					fillrectflag_y=quicks.idou[0][1]+1;
				}
			}
		}else if(startmuki==LEFT){
			if(endmuki==UP){
				if(flag1==true){
					fillrectflag_x=quicks.idou[0][0]-1;
					fillrectflag_y=quicks.idou[0][1]+1;
				}else{
					fillrectflag_x=quicks.idou[0][0]-1;
					fillrectflag_y=quicks.idou[0][1]-1;
				}
			}else if(endmuki==DOWN){
				if(flag1==true){
					fillrectflag_x=quicks.idou[0][0]-1;
					fillrectflag_y=quicks.idou[0][1]-1;
				}else{
					fillrectflag_x=quicks.idou[0][0]-1;
					fillrectflag_y=quicks.idou[0][1]+1;
				}
			}else if(endmuki==LEFT){
				if(flag1==true){
					fillrectflag_x=quicks.idou[0][0]-1;
					fillrectflag_y=quicks.idou[0][1]-1;
				}else{
					fillrectflag_x=quicks.idou[0][0]-1;
					fillrectflag_y=quicks.idou[0][1]+1;
				}
			}else if(endmuki==RIGHT){
				if(flag1==true){
					fillrectflag_x=quicks.idou[0][0]-1;
					fillrectflag_y=quicks.idou[0][1]+1;
				}else{
					fillrectflag_x=quicks.idou[0][0]-1;
					fillrectflag_y=quicks.idou[0][1]-1;
				}
			}
		}else if(startmuki==RIGHT){
			if(endmuki==UP){
				if(flag1==true){
					fillrectflag_x=quicks.idou[0][0]+1;
					fillrectflag_y=quicks.idou[0][1]-1;
				}else{
					fillrectflag_x=quicks.idou[0][0]+1;
					fillrectflag_y=quicks.idou[0][1]+1;
				}
			}else if(endmuki==DOWN){
				if(flag1==true){
					fillrectflag_x=quicks.idou[0][0]+1;
					fillrectflag_y=quicks.idou[0][1]+1;
				}else{
					fillrectflag_x=quicks.idou[0][0]+1;
					fillrectflag_y=quicks.idou[0][1]-1;
				}
			}else if(endmuki==LEFT){
				if(flag1==true){
					fillrectflag_x=quicks.idou[0][0]+1;
					fillrectflag_y=quicks.idou[0][1]+1;
				}else{
					fillrectflag_x=quicks.idou[0][0]+1;
					fillrectflag_y=quicks.idou[0][1]-1;
				}
			}else if(endmuki==RIGHT){
				if(flag1==true){
					fillrectflag_x=quicks.idou[0][0]+1;
					fillrectflag_y=quicks.idou[0][1]-1;
				}else{
					fillrectflag_x=quicks.idou[0][0]+1;
					fillrectflag_y=quicks.idou[0][1]+1;
				}
			}
		}
		
		if(flag1==true){//曲がり角座標の中で最も大きな座標、最も小さな座標を抜き出す。
			for(c=1;c<=po1;c++){
				if(cheakmax_x<cheak1[c][0]){
					cheakmax_x=cheak1[c][0];
				}
				if(cheakmax_y<cheak1[c][1]){
					cheakmax_y=cheak1[c][1];
				}
				if(cheakmin_x>cheak1[c][0]){
					cheakmin_x=cheak1[c][0];
				}
				if(cheakmin_y>cheak1[c][1]){
					cheakmin_y=cheak1[c][1];
				}
			}
		}else{
			for(c=1;c<=po2;c++){
				if(cheakmax_x<cheak2[c][0]){
					cheakmax_x=cheak2[c][0];
				}
				if(cheakmax_y<cheak2[c][1]){
					cheakmax_y=cheak2[c][1];
				}
				if(cheakmin_x>cheak2[c][0]){
					cheakmin_x=cheak2[c][0];
				}
				if(cheakmin_y>cheak2[c][1]){
					cheakmin_y=cheak2[c][1];
				}
			}
		}
		
		fillRects();//ラベリング法による　塗りつぶし
		fillLine();//囲まれた部分の旧境界線を消す
		
		for(i=0;i<640;i++){
			for(i2=0;i2<480;i2++){
				if(quicks.map[i][i2]==3){//切り取り中に通った軌跡
					quicks.map[i][i2]=2;
				}
			}
		}
		

		quicks.tempx_max=cheakmax_x;
		quicks.tempy_max=cheakmax_y;
		quicks.tempx_min=cheakmin_x;
		quicks.tempy_min=cheakmin_y;
		this.Back();//描画処理バックメモリ領域の更新 関数の呼び出し
		quicks.player1.kiridasi=false;//切り出しフラグを　OFFへ
		
		temp_min_x=637;//切り取り四角形座標判定変数の初期化
		temp_min_y=477;
		temp_max_x=1;
		temp_max_y=1;

		//スレッド再開
		if(quicks.time_infinity==false){
			quicks.time_thread.contine();
		}
		quicks.enemy_thread.contine();
		
	}
	
	//ライン塗りつぶしメソッド--------------------------------------------------------------------------------
	public void fillLine(){
		int i=0;
		int j=0;
		if(flag1==true){
			for(i=0;i<po1;i++){
				if(cheak1[i][0]==cheak1[i+1][0]){//xの列が同じ時
					if(cheak1[i][1]>cheak1[i+1][1]){
						for(j=cheak1[i+1][1];j<=cheak1[i][1];j++){
							quicks.map[cheak1[i][0]][j]=1;
						}
					}else{
						for(j=cheak1[i][1];j<=cheak1[i+1][1];j++){
							quicks.map[cheak1[i][0]][j]=1;
						}
					}
				}else if(cheak1[i][1]==cheak1[i+1][1]){//yの列が同じ時
					if(cheak1[i][0]>cheak1[i+1][0]){
						for(j=cheak1[i+1][0];j<=cheak1[i][0];j++){
							quicks.map[j][cheak1[i][1]]=1;
						}
					}else{
						for(j=cheak1[i][0];j<=cheak1[i+1][0];j++){
							quicks.map[j][cheak1[i][1]]=1;
						}
					}
				}
			}
		}else{
			for(i=0;i<po2;i++){
				if(cheak2[i][0]==cheak2[i+1][0]){//xの列が同じ時
					if(cheak2[i][1]>cheak2[i+1][1]){
						for(j=cheak2[i+1][1];j<=cheak2[i][1];j++){
							quicks.map[cheak2[i][0]][j]=1;
						}
					}else{
						for(j=cheak2[i][1];j<=cheak2[i+1][1];j++){
							quicks.map[cheak2[i][0]][j]=1;
						}
					}
				}else if(cheak2[i][1]==cheak2[i+1][1]){//yの列が同じ時
					if(cheak2[i][0]>cheak2[i+1][0]){
						for(j=cheak2[i+1][0];j<=cheak2[i][0];j++){
							quicks.map[j][cheak2[i][1]]=1;
						}
					}else{
						for(j=cheak2[i][0];j<=cheak2[i+1][0];j++){
							quicks.map[j][cheak2[i][1]]=1;
						}
					}
				}
			}
		}
		quicks.map[cheak1[po1][0]][cheak1[po1][1]]=2;//到着点を再度境界線「２」へ
		quicks.map[cheak1[0][0]][cheak1[0][1]]=2;//出発点を再度・・・
	}
	
	//ライン塗りつぶしメソッド--------------------------------------------------------------------------------
	public void fillLine(int cheak[][],int po1){
		int i=0;
		int j=0;
		if(flag1==true){
			for(i=0;i<po1;i++){
				if(cheak[i][0]==cheak[i+1][0]){//xの列が同じ時
					if(cheak[i][1]>cheak[i+1][1]){
						for(j=cheak[i+1][1];j<=cheak[i][1];j++){
							quicks.map[cheak[i][0]][j]=2;
						}
					}else{
						for(j=cheak[i][1];j<=cheak[i+1][1];j++){
							quicks.map[cheak[i][0]][j]=2;
						}
					}
				}else if(cheak[i][1]==cheak[i+1][1]){//yの列が同じ時
					if(cheak[i][0]>cheak[i+1][0]){
						for(j=cheak[i+1][0];j<=cheak[i][0];j++){
							quicks.map[j][cheak[i][1]]=2;
						}
					}else{
						for(j=cheak[i][0];j<=cheak[i+1][0];j++){
							quicks.map[j][cheak[i][1]]=2;
						}
					}
				}
			}
		}
		quicks.map[cheak[po1][0]][cheak[po1][1]]=2;//到着点を再度境界線「２」へ
		quicks.map[cheak[0][0]][cheak[0][1]]=2;//出発点を再度・・・
	}
	
	//ライン塗りつぶしメソッド--------------------------------------------------------------------------------
	public void fillLine(int cheak[][],int po1,int a){
		int i=0;
		int j=0;
		if(flag1==true){
			for(i=0;i<po1;i++){
				if(cheak[i][0]==cheak[i+1][0]){//xの列が同じ時
					if(cheak[i][1]>cheak[i+1][1]){
						for(j=cheak[i+1][1];j<=cheak[i][1];j++){
							quicks.map[cheak[i][0]][j]=2;
						}
					}else{
						for(j=cheak[i][1];j<=cheak[i+1][1];j++){
							quicks.map[cheak[i][0]][j]=2;
						}
					}
				}else if(cheak[i][1]==cheak[i+1][1]){//yの列が同じ時
					if(cheak[i][0]>cheak[i+1][0]){
						for(j=cheak[i+1][0];j<=cheak[i][0];j++){
							quicks.map[j][cheak[i][1]]=2;
						}
					}else{
						for(j=cheak[i][0];j<=cheak[i+1][0];j++){
							quicks.map[j][cheak[i][1]]=2;
						}
					}
				}
			}
		}
		quicks.map[cheak[po1][0]][cheak[po1][1]]=2;//到着点を再度境界線「２」へ
		quicks.map[cheak[0][0]][cheak[0][1]]=2;//出発点を再度・・・
	}
	
	//1フラグ立て塗りつぶし処理メソッド------------------------------------------------------------------------
	public void fillRects(){
		int i,j;
		
		if(flag1==true){
			//まず、自領域の曲がり角の座標を記録する
			for(i=0;i<po1;i++){
				x_line[i]=temp_line[i][0]=cheak1[i][0];
				y_line[i]=temp_line[i][1]=cheak1[i][1];
			}
			j=i;	//x_line,y_lineにデータを順番に保存するためにiの値を保存する
			//今回、切り取った曲がり角の座標を記録する
			magattaKazu1=i;
			for(i=0;i<quicks.magari-1;i++){
				x_line[i+j]=quicks.idou[i+1][0];
				y_line[i+j]=quicks.idou[i+1][1];
			}
		}else{
			//まず、自領域の曲がり角の座標を記録する
			for(i=0;i<po2;i++){
				x_line[i]=temp_line[i][0]=cheak2[i][0];
				y_line[i]=temp_line[i][1]=cheak2[i][1];
			}
			j=i;	//x_line,y_lineにデータを順番に保存するためにiの値を保存する
			//今回、切り取った曲がり角の座標を記録する
			magattaKazu1=i;
			for(i=0;i<quicks.magari-1;i++){
				x_line[i+j]=quicks.idou[i+1][0];
				y_line[i+j]=quicks.idou[i+1][1];
			}
		}
		
		magattaKazu = i+j;
		
		heapsort(x_line,i+j);//ヒープソート
		heapsort(y_line,i+j);
		
		x_num=ssort_d(x_line,i+j);//ダブっている数値を削除、x_numにはx_line配列の配列長が
		y_num=ssort_d(y_line,i+j);//ダブっている数値を削除、y_numにはy_line配列の配列長が
		
		//System.out.println("頂点座標");
		for(i = 0;i<x_num;i++){
			//System.out.println("x" +i + ":" + x_line[i]);
		}
		
		for(i = 0;i<y_num;i++){
			//System.out.println("y" + i + ":" + y_line[i]);
		}
		
		//下準備完了！！
		
		linepart_x=x_num-1;//横の四角の数
		linepart_y=y_num-1;//縦の四角の数
		
		rect_num = linepart_x * linepart_y;//四角形の数
		//System.out.println("rect_num : " + rect_num);//デバック
		
		
		
		for(i=1;i<x_num;i++){
			if(fillrectflag_x<x_line[i]){
				for(j=1;j<y_num;j++){
					if(fillrectflag_y<y_line[j]){
						firstflag = (j-1)*linepart_x + (i-1);
						fillrectflag[firstflag]=1;
						//System.out.println(firstflag);//デバック
						
						break;
					}	
				}
				break;
			}
		}
		
		
		cheak_border(firstflag);

		for(i=0;i<rect_num;i++){
			if(fillrectflag[i]==1){
				//System.out.println("------------------\nfillrect No." + i);//デバック
				fillpartRects(getrect_x(i,1),getrect_y(i,1),getrect_x(i,2),getrect_y(i,2));
			}
		}
		
		//fillLine(temp_line,magattaKazu);
		
		//fillLine(temp_line,magattaKazu1,1);
		
	}
	
	
	//４連結連結判定メソッド--------------------------------------------------------------------------
	void cheak_border(int rect_No){		
		int cheakrect_up=0;
		int cheakrect_down=0;
		int cheakrect_left=0;
		int cheakrect_right=0;
		
		if(rect_No-linepart_x>=0 && fillrectflag[rect_No-linepart_x]==0){
			cheakrect_up=1;
		}
		if(rect_No+linepart_x<rect_num && fillrectflag[rect_No+linepart_x]==0){
			cheakrect_down=1;
		}
		if(rect_No%linepart_x!=0 && fillrectflag[rect_No-1]==0){
			cheakrect_left=1;
		}
		if(rect_No%linepart_x!=linepart_x-1 && fillrectflag[rect_No+1]==0){
			cheakrect_right=1;
		}
		
		if(cheakrect_up==1){
			if(quicks.map[(getrect_x(rect_No,1)+getrect_x(rect_No,2))/2]
			   [getrect_y(rect_No,1)]!=3&&
			   quicks.map[(getrect_x(rect_No,1)+getrect_x(rect_No,2))/2]
			   [getrect_y(rect_No,1)]!=2){
				fillrectflag[rect_No-linepart_x]=1;
				stack.addElement(new Integer(rect_No-linepart_x));
			}else{
				fillrectflag[rect_No-linepart_x]=2;
			}
		}
		if(cheakrect_down==1){
			if(quicks.map[(getrect_x(rect_No,1)+getrect_x(rect_No,2))/2]
			   [getrect_y(rect_No,2)]!=3&&
			   quicks.map[(getrect_x(rect_No,1)+getrect_x(rect_No,2))/2]
			   [getrect_y(rect_No,2)]!=2){
				fillrectflag[rect_No+linepart_x]=1;
				stack.addElement(new Integer(rect_No+linepart_x));
			}else{
				fillrectflag[rect_No+linepart_x]=2;
			}
		}
		if(cheakrect_left==1){
			if(quicks.map[getrect_x(rect_No,1)]
			   [(getrect_y(rect_No,1)+getrect_y(rect_No,2))/2]!=3&&
			   quicks.map[getrect_x(rect_No,1)]
			   [(getrect_y(rect_No,1)+getrect_y(rect_No,2))/2]!=2){
				fillrectflag[rect_No-1]=1;
				stack.addElement(new Integer(rect_No-1));
			}else{
				fillrectflag[rect_No-1]=2;
			}
		}
		if(cheakrect_right==1){
			if(quicks.map[getrect_x(rect_No,2)]
			   [(getrect_y(rect_No,1)+getrect_y(rect_No,2))/2]!=3&&
			   quicks.map[getrect_x(rect_No,2)]
			   [(getrect_y(rect_No,1)+getrect_y(rect_No,2))/2]!=2){
				fillrectflag[rect_No+1]=1;
				stack.addElement(new Integer(rect_No+1));
			}else{
				fillrectflag[rect_No+1]=2;
			}
		}
		
		while(stack.isEmpty()==false){
			cheak_border(((Integer)stack.pop()).intValue());
			
		}		
	}
	//-----------------------------------------------------------------------------------------------------
	
	//分割された四角形塗りつぶしメソッド----------------------------------------------------------------------
	void fillpartRects(int min_x,int min_y,int max_x,int max_y){
		int i,j;
		for(i=min_x;i<=max_x;i++){
			for(j=min_y;j<=max_y;j++){
				if(quicks.map[i][j]!=3){
					quicks.map[i][j]=1;
				}
			}
		}
	}	
	//-------------------------------------------------------------------------------------------------------
	
	//指定された四角形の頂点のｘ座標を返すメソッド--------------------------------------------------------------
	//rect_Noには四角形の番号が,dataには識別用引数が1なら左側の座標、2なら右側の座標を返す
	
	int getrect_x(int rect_No,int data){
		
		int ab_rect_No;		//絶対値の四角形番号を格納する変数
		
		if(data == 1){
			ab_rect_No=rect_No%linepart_x;
			//System.out.println("x_line[ab_rect_No]<------" + ab_rect_No);
			//System.out.println("data ==1 x_line[ab_rect_No]:" + x_line[ab_rect_No]);
			return x_line[ab_rect_No];
		}else if(data == 2){
			ab_rect_No=rect_No%linepart_x;
			//System.out.println("x_line[ab_rect_No+1]<------" + (ab_rect_No +1));
			//System.out.println("data ==2 x_line[ab_rect_No+1]:" + x_line[ab_rect_No+1]);
			return x_line[ab_rect_No+1];
		}else{
			return 0;
		}
	}
	//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	
	//指定された四角形の頂点のｙ座標を返すメソッド---------------------------------------------------------------
	//rect_Noには四角形の番号が,dataには識別用引数が1なら上側の座標、2なら下側の座標を返す
	
	int getrect_y(int rect_No,int data){
		
		int ab_rect_No;		//絶対値の四角形番号を格納する変数
		
		if(data == 1){
			ab_rect_No=rect_No/linepart_x;
			//System.out.println("y_line[ab_rect_No]<------" + ab_rect_No);
			//System.out.println("data ==1 y_line[ab_rect_No]:" + y_line[ab_rect_No]);
			return y_line[ab_rect_No];
		}else if(data == 2){
			ab_rect_No=rect_No/linepart_x;
			//System.out.println("y_line[ab_rect_No+1]<------" + (ab_rect_No +1));
			//System.out.println("data ==2 y_line[ab_rect_No+1]:" + y_line[ab_rect_No+1]);
			return y_line[ab_rect_No+1];
		}else{
			return 0;
		}		
	}
	//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	
	
	//描画処理バックメモリ領域の更新---------------------------------------------------------------------------
	private void Back(){
		quicks.kakomi_sound.play();//囲み完了の音を鳴らす
		
		this.quicks.backbuf3.drawImage(this.quicks.back_surface2,0,0,null);//前回記憶背景画像の表示

		for(i=0;i<=rect_num;i++){
			drawPartRects(getrect_x(i,1),getrect_y(i,1),getrect_x(i,2),getrect_y(i,2),i);
		}
		this.quicks.backbuf3.setColor(Color.white);//カラーを白にセット
		for(i=0;i<=quicks.magari-2;i++){
			this.quicks.backbuf3.drawLine(quicks.idou[i][0],quicks.idou[i][1],quicks.idou[i+1][0],quicks.idou[i+1][1]);
		}
		
		//初期化
		for(i=0;i<magattaKazu;i++){
			temp_line[i][0]=temp_line[i][1]=0;
			x_line[i]=0;
			y_line[i]=0;
		}
		for(i=0;i<rect_num+1;i++){
			fillrectflag[i]=0;
		}
		magattaKazu=0;
		magattaKazu1=0;
		
		this.quicks.backbuf2.drawImage(this.quicks.back_surface2,0,0,null);//出来た絵を裏バッファへ転送
		
		quicks.parsent=(par/290000.0)*100.0;//何％切ったか統計を出す
			}//-------------------------------------------------------------------------------------------------------
	
	
	//描画する------------------------------------------------------------------------------------------------
	private void drawPartRects(int min_x,int min_y,int max_x,int max_y,int z){
		if(fillrectflag[z]==1){//フラグが立っている四角形領域であれば塗る
			this.quicks.backbuf3.drawImage(this.quicks.backcg,min_x,min_y,max_x+1,max_y+1,min_x,min_y,max_x+1,max_y+1,null);
			par+=((max_x - min_x) * (max_y - min_y));//面積分　囲み領域％へ追加
		}
	}//--------------------------------------------------------------------------------------------------------
	
	////////////// ヒープソート//////////////////////////////////////////////////////////////////
	void heapsort(int line[],int line_count){
		int    i, j,k;
		int temp;
		for( i = line_count / 2 - 1; i >= 0; i-- ){
			for( j = i; (k = 2 * j + 1) < line_count; j = k){
				if( k + 1 < line_count ){
					if(line[k]<line[k + 1]) k++;
				}
				if(line[j]>=line[k]) break;
				temp = line[j];
				line[j] = line[k];
				line[k] = temp;
			}
		}
		for( i = line_count - 1; i > 0 ; i--){
			temp = line[0];
			line[0] = line[i];
			line[i] = temp;
			for( j = 0; (k = 2 * j + 1) < i; j = k){
				if( k + 1 < i ){
					if(line[k]<line[k + 1]) k++;
				}
				if(line[j]>=line[k] )break;
				temp = line[j];
				line[j] = line[k];
				line[k] = temp;
			}
		}
	}
	/*******************************************************************************************************************/
	
	//------線形ソート（ダブっている数値を削除、配列を詰める）---------------------------------------------
	
	int ssort_d(int a[], int n)
	{
		int i,j;
		
		for(i=0;i<n;i++){
			if(a[i]==a[i+1]){
				for(j=i;j<n;j++){
					a[j+1]=a[j+2];
				}
				i--;
				n--;
			}
		}
		return n;
	}
	//--------------------------------------------------------------------------------------------------
	
}
/////////////////////////////////////////////////////////////////////////////////////////////////////


//プレイヤー操作クラス////////////////////////////////////////////////////////////////////////////
class player{
	final int UP=5,DOWN=6,RIGHT=7,LEFT=8;//上下左右を簡単な書式で制御できるようにしておく
	public int x,y;//位置情報
	public int width=10,height=10;//画像の幅、高さ　情報
	public int kiri_width=10,kiri_height=10;//切り取り中画像の幅、高さ　情報
	public int muki;//向き情報
	public int start_muki;//切り出し初期向き
	public int end_muki;//切り出し終了向き
	public Image gazou;//画像情報
	public Image gazou_kiri;//切り出し中の画像
	public boolean kiridasi=false;//切り出しフラグ
	public int kisuu=5;//残り機体の数
	public int sokudo=2;//プレイヤーの移動速度
	
	player(){//コンストラクタ　初期位置の決定
		this.x=1;
		this.y=1;
	}
	player(int X,int Y){//初期位置を指定することもできる。
		this.x=X;
		this.y=Y;
	}
}
/////////////////////////////////////////////////////////////////////////////////////////////////
//敵操作クラス///////////////////////////////////////////////////////////////////////////////////
class enemy implements Runnable{
	public quicks quicks = null;//Quicksクラスからの参照渡し
	public int x,y;//位置情報
	public int muki_x;//向き情報
	public int muki_y;
	public int width=1;//横幅
	public int height=1;//縦幅
	public int sokudo;//敵の移動速度
	public int logic;//CPU アルゴリズム　行動パターン何で動作するか？
	public boolean dead=false;//死亡フラグ
	public Image gazou;//敵　画像読み込み用
	public int i;//temp用
	enemy(quicks q){//コンストラクタ　初期位置の決定
		this.quicks = q;//Quicksクラスからの参照渡し
		this.x=(int)(Math.random() * (double)400);//ランダム位置
		this.y=(int)(Math.random() * (double)400);//ランダム位置
		logic=(int)((Math.random() * (double)4)+1);
		sokudo=2;
		if(logic<2){muki_x=muki_y=1;}
		if(logic==2){muki_x=1;muki_y=-1;}
		if(logic==3){muki_x=muki_y=-1;}
		if(logic>3){muki_x=-1;muki_y=1;}
	}
	enemy(quicks q,int X,int Y,int LOGIC,int SOKUDO){//初期位置を指定することもできる。
		this.quicks = q;//Quicksクラスからの参照渡し
		this.x=X;
		this.y=Y;
		this.logic=LOGIC;
		this.sokudo=SOKUDO;
		if(logic<2){muki_x=muki_y=1;}
		if(logic==2){muki_x=1;muki_y=-1;}
		if(logic==3){muki_x=muki_y=-1;}
		if(logic>3){muki_x=-1;muki_y=1;}
	}
	public void run(){
		try{
				if(quicks.gameover_flag==false){
					if(dead==false){//敵が生きているときだけ判定　死んでいるときはこの処理は飛ばす
						i=sokudo;
						while(i>0){
							i--;
							if(quicks.map[(x+(width/2))][(y+(height/2))]==1){
								dead=true;//死亡
								quicks.butukari_sound.play();//ぶつかり音発生
							}
							else if(quicks.map[x][y]==3||quicks.map[x+width][y]==3||quicks.map[x][y+height]==3||quicks.map[x+width][y+height]==3||
									(quicks.player1.kiridasi==true && !(quicks.player1.x>(this.x+this.width) || quicks.player1.y>(this.y+this.height) || (quicks.player1.x+quicks.player1.width)<this.x || (quicks.player1.y+quicks.player1.height)<this.y ))){//自機の切り取り線軌跡にぶつかったら
								quicks.kill.dead();//死亡処理
							}
							if(muki_x==1){//右向き
								if(quicks.map[x+1+width][y]==2||quicks.map[x+1+width][y+height]==2){//敵が自陣地境界線に触れたときの処理
									muki_x=-1;
								}
								else{
									x++;
								}
							}
							else if(muki_x==-1){//左向き
								if(quicks.map[x-1][y]==2||quicks.map[x-1][y+height]==2){//敵が自陣地境界線に触れたときの処理
									muki_x=1;
								}
								else{
									x--;
								}
							}
							if(muki_y==1){//下向き
								if(quicks.map[x][y+1+height]==2||quicks.map[x+width][y+1+height]==2){//敵が自陣地境界線に触れたときの処理
									muki_y=-1;
								}
								else{
									y++;
								}
							}
							else if(muki_y==-1){//上向き
								if(quicks.map[x][y-1]==2||quicks.map[x+width][y-1]==2){//敵が自陣地境界線に触れたときの処理
									muki_y=1;
								}
								else{
									y--;
								}
							}
						}
					}
				}
		}
		catch(Exception e){}
	}
}
/////////////////////////////////////////////////////////////////////////////////////////////////////


//死亡　処理クラス///////////////////////////////////////////////////////////////////////////////////////
class sibou{
	public quicks quicks = null;
	public int i,i2;
	public int temp_max_x=1,temp_max_y=1,temp_min_x=637,temp_min_y=477;
	
	public sibou(quicks q){//コンストラクタ　Quicksクラスからの参照渡し
		this.quicks = q;
	}
	public void dead(){
		
		quicks.idou[quicks.magari][0]=quicks.player1.x;//死んだ地点の座標を保持
		quicks.idou[quicks.magari][1]=quicks.player1.y;
		quicks.magari++;
		quicks.butukari_sound.play();//ぶつかった音を鳴らす
		quicks.player1.kisuu--;//自機数一つ減らす
		quicks.player1.kiridasi=false;//切り出しフラグをＯＦＦ
		for(i=0;i<quicks.magari;i++){
			if(temp_max_x<quicks.idou[i][0]){
				temp_max_x=quicks.idou[i][0];
			}
			if(temp_max_y<quicks.idou[i][1]){
				temp_max_y=quicks.idou[i][1];
			}
			if(temp_min_x>quicks.idou[i][0]){
				temp_min_x=quicks.idou[i][0];
			}
			if(temp_min_y>quicks.idou[i][1]){
				temp_min_y=quicks.idou[i][1];
			}
		}

		for(i=temp_min_x;i<=temp_max_x;i++){
			for(i2=temp_min_y;i2<=temp_max_y;i2++){
				if(quicks.map[i][i2]==3){//3であれば　(切り取り軌跡であれば)
					quicks.map[i][i2]=0;//未開拓領域へ
				}
			}
		}
		//出発点も０になってしまっているので直す必要がある/////////////////////////////////////
		i=quicks.idou[0][0];
		i2=quicks.idou[0][1];
		quicks.map[i][i2]=2;//出発点だけ　あらためて２に戻す。/////////////////////////////////
		
		temp_min_x=637;//切り取り四角形座標判定変数の初期化
		temp_min_y=477;
		temp_max_x=1;
		temp_max_y=1;
		
		quicks.player1.x=quicks.idou[0][0];//プレイヤーを出発点に戻す
		quicks.player1.y=quicks.idou[0][1];
	}
}////////////////////////////////////////////////////////////////////////////////////////////////////////////
/******************************************************************************************************
�uJava�N�C�b�N�X�J��SDK�v1.4
����^�r�c�v���_�N�V����
�J�����FEclipse 3.1 
                & JDK 5.0.1 Released by SunMicrosystems Company.
�Ώۊ��FWindows98/Me/2000/NT/XP JAVA���s�Ή�IE
�K�v�Œ���FCPU     333MHz
              ������  64MB
�������@�@�FCPU     1GHz
�@�@�@�@�@�@�@�������@128MB
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
/* �L�[�{�[�h���͂��ł���A�v���b�gKeyTest�N���X�̒�` */
public class quicks extends Applet implements KeyListener,Runnable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*�A�v���b�g�N���X�̌p���y�тɃX���b�h�N���X�̃C���^�t�F�[�X�̃C���v�������g
	�L�[�C�x���g�擾�p���X�i�[�C���^�t�F�[�X�̃C���v�������g*/	
	
	//�ϐ���`/////////////////////////////////////////////////////////////////////////////
	final int UP=5,DOWN=6,RIGHT=7,LEFT=8;//�㉺���E���ȒP�ȏ����Ő���ł���悤�ɂ��Ă���
	int clear_menseki=85;//�N���A�[�ʐρ@��%�؂�ƃN���A�[�ɂȂ邩
	public int FRAME_LATE=60;//�t���[�����[�g�@�b�ԉ��x��ʂ��ĕ`��X�V���邩
	public AudioClip kiri_sound;//�؂��蒆�̉�
	public AudioClip kakomi_sound;//�͂݊�����
	public AudioClip butukari_sound;//�Ԃ������Ƃ��̉�
	public AudioClip gameover_sound;//�Q�[���I�[�o�[�̂Ƃ��̉�
	public AudioClip stageclear_sound;//�X�e�[�W�N���A�[�̂Ƃ��̉�
	public AudioClip start_sound;//�X�^�[�g��
	public MediaTracker mt;//���f�B�A�g���b�J�[
	public Font default_font;//�f�t�H���g�t�H���g
	public int font=24;//�t�H���g�T�C�Y
	public String color ="";//�t�H���g�J���[
	public boolean gameover_flag=false;//�Q�[���I�[�o�[���ǂ����̃t���O
	public boolean clear_gazou=false;//�N���A�[�摜�̃X�C�b�`
	public String ss="KeyTyped: ";
	public boolean gameplay=true;
	public boolean clear=false;//�N���A�[�t���O
	public String gameover_massage="";//�Q�[���I�[�o�[�����b�Z�[�W
	public String stage_clear_massage="";//�X�e�[�W�N���A�[���b�Z�[�W
	public String gameclear_URL="";//�Q�[���N���A�[���ɔ��URL
	public boolean jump_URL=false;//�Q�[���N���A�[���Ɏw��URL�֔�Ԃ��ǂ���
	public int time_rimit=1000;//����
	public int main_time=1000;
	public boolean time_hurry=false;//�������Ԃ��܂��Ă����Ƃ��̃t���O
	public boolean time_infinity=false;//���Ԑ����Ȃ��t���O
	public int ct=0,i=0,i2=0;
	public int tempx_max=0,tempy_max=0,tempx_min=0,tempy_min=0;//�͂ݔ���N���X�Ŏg�����߂̑��ϐ�
	public int magari=0;//�ړ��p�񐔕ϐ�
	public enemy_manager enemy_thread = null;//�G�X���b�h
	public time_manager time_thread = null;//���ԊǗ��p�N���X
	public int teki_kazu=3;//�G�̐�
	public Image backcg;//�w�i�`��p
	public Image frontcg;//�O�ʕ`��p
	public byte [][] map=new byte[640][480];//�}�b�v
	public int [][] idou=new int[2000][2];//�����̈ړ��O��[]�񕪂̋Ȃ���p��ۑ�
	public double parsent=0;//��%�؂�����
	public int kisuu=5;//���@���C�t�|�C���g
	public sibou kill= new sibou(this);//���S�N���X Quicks�N���X�̃C���X�^���X(this)���Q�Ɠn��
	public kakomi_hantei kakomi = new kakomi_hantei(this);//�͂ݔ���N���X��Quicks�N���X�̃C���X�^���X(this)���Q�Ɠn��
	
	public player player1;//���@�N���X
	public enemy teki [] = new enemy[7];//�G�z��m�ہA�C���X�^���X�͂܂���������ĂȂ�
	public Image pri_surface=null;//�O�ʕ`��p�I�u�W�F�N�g�@�v���C�}���T�[�t�F�X
	public Image back_surface=null;//���ŉ�ʏ��������O�ɍς܂���̂Ɏg��
	public Image back_surface2=null;
	public Image back_surface3=null;
	public Graphics backbuf=null;//�w�i�`�惁�����p�i�_�u���o�b�t�@�����O)
	public Graphics backbuf2=null;//�w�i�`��p����������2
	public Graphics backbuf3=null;//�w�i�`��p����������3
	public Graphics backbuf4=null;//�w�i�`��p����������4
	/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//�A�v���b�g�I�����Ɏ��s����郁�\�b�h///////////////////////////////////////////////////////////////
	public void destroy(){
		//�`��̈���
		backbuf.dispose();
		backbuf2.dispose();
		backbuf3.dispose();
		backbuf4.dispose();
		pri_surface.flush();
		back_surface.flush();
		back_surface2.flush();
		back_surface3.flush();

		//�X���b�h�j��
		enemy_thread.stop();
		time_thread.stop();
		//�L�[���X�i�[�̐؂藣��
		removeKeyListener(this);
		//���g�A�v���b�g�X���b�h�̒�~
		this.stop();
	}
	
	//�A�v���b�g�N�����ɍŏ��Ɏ��s����郁�\�b�h/////////////////////////////////////////////////////////
	public void init(){
		Dimension d=getSize();//�����ŃA�v���b�g�̃E�B���h�E�T�C�Y���擾
		pri_surface=createImage(d.width,d.height);//�A�v���b�g�E�B���h�E�T�C�Y�Ɠ����傫���̕`��̈�m��
		backbuf=pri_surface.getGraphics();
		back_surface=createImage(d.width,d.height);
		backbuf2=back_surface.getGraphics();
		back_surface2=createImage(d.width,d.height);
		backbuf3=back_surface2.getGraphics();
		back_surface3=createImage(d.width,d.height);
		backbuf4=back_surface3.getGraphics();

		addKeyListener(this);//�L�[���X�i�o�^
		requestFocus();//�t�H�[�J�X�𓾂�
		
		
		String s_init=null;
		Integer temp_init = new Integer(3);
		s_init=getParameter("teki_kazu");
		if (s_init != null){
			temp_init = Integer.valueOf(s_init);
			if(s_init.length()>2){//3���ȏ�̐����������珬��������
				temp_init=Integer.valueOf(7);
			}

			if(temp_init.intValue() > 7){//�傫������ƍ���̂ň��S����
				temp_init=Integer.valueOf(7);
			}
			else if(temp_init.intValue()<1){
				temp_init=Integer.valueOf(1);
			}
		}
		teki_kazu=temp_init.intValue();
		
		player1=new player(1,1);//���@�C���X�^���X�@�����ʒu��^���Đ���
		
		//�G�C���X�^���X�쐬
		for (int i=0;i<teki_kazu;i++){
			teki[i] = new enemy(this);
		}

		clear=false;//�N���A�[�t���O
		gameover_flag=false;//�Q�[���I�[�o�[���ǂ����̃t���O
		clear_gazou=false;
		ss="KeyTyped: ";
		ct=i=i2=0;
		tempx_max=tempy_max=tempx_min=tempy_min=0;
		parsent=0;//�؂���%�@���O�ɏ�����
		kakomi.par=0;//�؂���%���O�ɏ�����
		
		mt = new MediaTracker(this);//���f�B�A�g���b�J�[�̐���
		LoadImage();//�摜�ǂݍ��݊֐������s���đS�Ẳ摜��ǂݍ���ł���
		
		map_clear();//�}�b�v���̏�����
		Back();//�`�揈���o�b�N�������̈�̏�����
		
		player1.width=player1.gazou.getWidth(this);//���@�摜�̕��擾
		player1.height=player1.gazou.getHeight(this);//���@�摜�̍����擾
		player1.kiri_width=player1.gazou_kiri.getWidth(this);//���@�؂��蒆�摜�̕��擾
		player1.kiri_height=player1.gazou_kiri.getHeight(this);//���@�؂��蒆�摜�̍����擾
		
		for (int i=0;i<teki_kazu;i++){
			teki[i].width = teki[i].gazou.getWidth(this);//�G�摜�̕��擾
			teki[i].height=teki[i].gazou.getHeight(this);//�G�摜�̍����擾
		}
		
		main_time=time_rimit;		
		time_thread=new time_manager(this);//���ԊǗ��p�X���b�h����
		time_thread.start();
		
		enemy_thread = new enemy_manager(this);//�G�Ǘ��X���b�h����
		enemy_thread.start();
	}
	
	//�}�b�v����������//////////////////////////////////////////////////////////////////////////////////
	public void map_clear(){
		for(int x=0;x<640;x++){//�}�b�v����S�ĂO��
			for(int y=0;y<480;y++){
				map[x][y]=0;
			}
		}
		for(int x2=1;x2<640;x2++){//�㉺���E�̕ӂ��ړ��\�̈�ɂ���@�ŏ��̎����̐w�n����
			map[x2][1]=2;
			map[x2][477]=2;
		}
		for(int y2=1;y2<480;y2++){
			map[1][y2]=2;
			map[637][y2]=2;
		}
		for(int x2=0;x2<640;x2++){//�㉺���E�̕ӂ��͂��ςݗ̈�Ɠ����̏����ɂ���@�Ǒ���
			map[x2][0]=1;
			map[x2][479]=map[x2][478]=1;
		}
		for(int y2=0;y2<480;y2++){
			map[0][y2]=1;
			map[639][y2]=map[638][y2]=1;
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	//�摜�ǂݍ��݊֐�///////////////////////////////////////////////////////////////////////////////////////////////
	public void LoadImage(){
		String s=null;
		Integer temp = new Integer(0);

		s = getParameter("back");//�w�i�摜 jpeg�摜
		if (s == null) s = "CG/back.jpg";
		backcg = getImage(getDocumentBase(), s);
		mt.addImage(backcg,0);
		
		s = getParameter("front");//�O�ʉ摜 jpeg�摜
		if (s == null)s = "CG/front.jpg";
		frontcg = getImage(getDocumentBase(), s);
		mt.addImage(frontcg,0);
		s = getParameter("player1");//���@�摜
		if (s == null) s = "CG/player1.gif";
		player1.gazou = getImage(getDocumentBase(), s);
		mt.addImage(player1.gazou,1);
		s = getParameter("player1_kiri");//���@�摜
		if (s == null) s = "CG/player1_kiri.gif";
		player1.gazou_kiri = getImage(getDocumentBase(), s);
		mt.addImage(player1.gazou_kiri,1);
		
		for(int i=0;i<teki_kazu;i++){
			s = getParameter("teki" + String.valueOf(i));//�G�摜 gif�摜
			if (s == null) s = "CG/teki" + String.valueOf(i) + ".gif";
			teki[i].gazou = getImage(getDocumentBase(), s);
			mt.addImage(teki[i].gazou,1);
		}
		
		
		stage_clear_massage = getParameter("stage_clear_massage");//�X�e�[�W�N���A�[���b�Z�[�W
		if (stage_clear_massage == null) stage_clear_massage = "�X�e�[�W�N���A�[�I�I";
		gameover_massage = getParameter("gameover_massage");//�Q�[���I�[�o�[���b�Z�[�W
		if (gameover_massage == null) gameover_massage = "GAME OVER";
		gameclear_URL = getParameter("gameclear_URL");//URL��������Ă�����
		if (gameclear_URL != null){
			jump_URL = true;//��������Ă�����URL�����W�����v�@�\�̃t���O�����Ă�
		}
		else if(gameclear_URL ==null){
			gameclear_URL = "./next_stage/index.html";//������Ă��Ȃ�������f�t�H���g�̃A�h���X�֔�Ԏd�g��
			jump_URL = true;
		}
		if(gameclear_URL.equals("no")==true){
			jump_URL=false;//URL�֔�΂Ȃ�
		}

	
		default_font = new Font("�l�r ����",Font.PLAIN,font);//�f�t�H���g�̃T�C�Y
		
		s = getParameter("kiri");//�e�퉹���ǂݍ���
		if (s == null) s = "sound/kiri.wav";
		kiri_sound = getAudioClip(getDocumentBase(), s);
		s = getParameter("kakomi");//�e�퉹���ǂݍ���
		if (s == null) s = "sound/kakomi.wav";
		kakomi_sound = getAudioClip(getDocumentBase(), s);
		s = getParameter("butukari");//�e�퉹���ǂݍ���
		if (s == null) s = "sound/butukari.wav";
		butukari_sound = getAudioClip(getDocumentBase(), s);
		s = getParameter("gameover");//�e�퉹���ǂݍ���
		if (s == null) s = "sound/gameover.wav";
		gameover_sound = getAudioClip(getDocumentBase(), s);
		s = getParameter("stageclear");//�e�퉹���ǂݍ���
		if (s == null) s = "sound/stageclear.wav";
		stageclear_sound = getAudioClip(getDocumentBase(), s);
		s = getParameter("start");//�e�퉹���ǂݍ���
		if (s == null) s = "sound/start.wav";
		start_sound = getAudioClip(getDocumentBase(), s);

		s=getParameter("time");
		temp = Integer.valueOf(1000);//�Ƃ肠�����ݒ肵�Ȃ��Ƃ����Ȃ�time_thread�ɂ�莞�Ԑ؂ꁨGameover
		if (s != null){//���Ԑ���
			temp = Integer.valueOf(s);
			if(s.length()>3){//4���ȏ�̐����������珬��������
				temp = Integer.valueOf(1000);
			}
			
			if(temp.intValue()==0){
				time_infinity=true;//���Ԑ����Ȃ�
			}

		}
		else if(s==null){
			time_infinity=true;//���Ԑ����Ȃ�
		}
		time_rimit=temp.intValue();
		main_time=temp.intValue();		
		
		FRAME_LATE = 30;
		s=getParameter("frame_late");
		if (s != null){//�t���[�����[�g
			temp = Integer.valueOf(s);
			if(s.length()>4){//4���ȏ�̐����������珬��������
				temp=Integer.valueOf(1000);
			}
			
			if(temp.intValue()>1000){//��������ƍ���̂ň��S����
				temp=Integer.valueOf(1000);
			}
			else if(temp.intValue()<1){
				temp=Integer.valueOf(1);
			}
			FRAME_LATE=(temp.intValue() + 5);//�Œ�ł�5
		}
		
		
		for (int i=0;i<teki_kazu;i++){
			s = getParameter("teki" + String.valueOf(i) + "_speed");
			if (s != null){
				temp = Integer.valueOf(s);
				if(s.length()>2){//4���ȏ�̐����������珬��������
					temp=Integer.valueOf(10);
				}

				if(temp.intValue()>10){//��������ƍ���̂ň��S����
					temp=Integer.valueOf(10);
				}
				else if(temp.intValue()<0){//�}�C�i�X�͂Ȃ�
					temp=Integer.valueOf(1);
				}
				teki[i].sokudo=temp.intValue();
			}
		}
		
		
		s=getParameter("parsent");
		if (s != null){
			//getParameter("parsent").valueOf(temp);//��%�؂�ƃN���A�[�ɂȂ邩
			temp = Integer.valueOf(s);
			if(s.length()>3){//3���ȏ�̐����������珬��������
				temp=Integer.valueOf(100);
			}

			if(temp.intValue()>100){//�傫������ƍ���̂ň��S����
				temp=Integer.valueOf(100);
			}
			else if(temp.intValue()<1){
				temp=Integer.valueOf(1);
			}
			clear_menseki=temp.intValue();
		}
		s=getParameter("kisuu");
		if (s != null){
			//getParameter("kisuu").valueOf(temp);//���@���C�t�|�C���g
			temp = Integer.valueOf(s);
			if(s.length()>3){//3���ȏ�̐����������珬��������
				temp=Integer.valueOf(99);
			}
			if(temp.intValue()>100){//�傫������ƍ���̂ň��S����
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
			if(s.length()>3){//3���ȏ�̐����������珬��������
				temp=Integer.valueOf(72);
			}

			if(temp.intValue()>72){//�傫������ƍ���̂ň��S����
				temp=Integer.valueOf(72);
			}
			else if(temp.intValue()<12){
				temp=Integer.valueOf(12);
			}
			font=temp.intValue();
		}
		
		s=getParameter("color");
		if (s != null){
			if(s.length()==7){//3���ȏ�̐����������珬��������
				color=s.substring(1);
				System.err.println(color); 
			}else{
				color=null;
			}
		}
		
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public void Back(){//�`�揈���o�b�N�������̈�̏�����---------------------------------------------------
		try {
			mt.waitForAll();//�S�Ẳ摜��ǂݍ��ނ܂őҋ@
		} catch (Exception e) {}
		
		this.backbuf2.drawImage(this.frontcg,0,0,null);									
		this.backbuf2.setColor(Color.white);//�J���[�𔒂ɃZ�b�g
		this.backbuf3.drawImage(this.frontcg,0,0,null);									
		this.backbuf3.setColor(Color.white);//�J���[�𔒂ɃZ�b�g
		for(i=1;i<639;i++){//���̗̈拫�E��(2�̗̈�j�͔��ɓh��
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
		backbuf3.setColor(Color.blue);//�J���[��ɃZ�b�g
		backbuf3.drawRect(0,0,639,479);//��ʈ�Ԓ[�̗��Ő�������
		backbuf3.drawRect(638,0,638,479);
		backbuf3.drawRect(0,478,639,478);
		//��Ԓ[�����̕��������炽�߂Ĕ��œh��Ȃ����B
		backbuf.setFont(default_font);
		backbuf.setColor(Color.white);//�J���[�𔒂ɃZ�b�g
		backbuf2.drawImage(back_surface2,0,0,null);
		
	}//-------------------------------------------------------------------------------------------------------
	
	
	//�Q�[�����X�^�[�g�֐�/////////////////////////////////////////////////////////////////////////////
	public void restart(){
		
		for(int i=0;i<teki_kazu;i++){//�G�S�������Ԃ点��
			teki[i].dead = false;
		}
		
		clear_gazou=false;
		gameover_flag=false;//�Q�[���I�[�o�[�t���O��������
		if(time_infinity==false){//���Ԑ����t���O�������
			main_time=time_rimit;//���Ԑ�������
			time_hurry=false;
			time_thread.contine();
		}
		ss="KeyTyped: ";
		player1.kisuu=kisuu;
		player1.x=1;//�v���C���[1���̏�����
		player1.y=1;
		player1.kiridasi=false;
		ct=i=i2=0;
		tempx_max=tempy_min=tempx_min=tempy_min=0;
		parsent=0;//�؂���%�@���O�ɏ�����
		kakomi.par=0;
		
		map_clear();//�}�b�v���̏�����
		//if (bgm_sound != null) bgm_sound.loop();// BGM��ٰ�ߍĐ�
		start_sound.play();//�X�^�[�g����炷
		Back();//�`�揈���o�b�N�������̈�̏�����
		gameplay=true;
		contine();
		
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	//�����R�[�h���o///////////////////////////////////////////////////////////////////////////////////
	public void keyTyped(KeyEvent e){//�L�[�����͂��ꂽ��
		String ss="";
		ss=ss+e.getKeyChar();

		
		if(ss.equals("r")==true){// �uR�v��������
			restart();//�Q�[���ċN��
		}
		
		
		if(gameover_flag==true){//�Q�[���I�[�o�[�̎�
			restart();//�Q�[�������X�^�[�g����
		}
		else if(clear==true){//�N���A�[�̎�
			clear_gazou=true;//�N���A�[�����̂ŉ摜��S�������
			if(jump_URL==true){//URL�w�肪����̂ł���΂����֔��
				try {
					this.getAppletContext().showDocument( new URL(getDocumentBase(), gameclear_URL ));
				} catch (Exception error) {
					System.err.println("URL JUMP Error !!! : "+ gameclear_URL );
				}
			}
			else{//URL�w�肪�Ȃ��Ƃ�
				repaint();//�`��
			}
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	//���z�L�[�R�[�h���o///////////////////////////////////////////////////////////////////////////////
	public void keyPressed(KeyEvent e){//�L�[��������Ă�����
		if(gameover_flag==false && clear==false){//�Q�[���I�[�o�[�A�X�e�[�W�N���A�[�̎��͂����̏����͂��Ȃ�
			int cd=e.getKeyCode();
			boolean ctrl=false;
			if(magari>=2000){
				kill.dead();
				magari=0;//�Ȃ���ϐ���999�𒴂�����i999��ȏ�Ȃ�������j�O�ɖ߂�
			}
			if((e.getModifiers() & InputEvent.CTRL_MASK)!= 0) {
				ctrl=true;//�؂�o�����m�Ɏg��
			}
			
			
			if(player1.kiridasi==false){//�܂��؂�o���Ă��Ȃ���
				if(ctrl==true){//�؂�o���{�^�������Ă�����؂�o�����J�n����
					magari=0;//������
					idou[magari][0]=player1.x;
					idou[magari][1]=player1.y;
					magari++;//���Ȃ��������Ƃɂ���
				}
			}
			
			
			if(player1.kiridasi==true){//�؂����ƒ�//////////////////////////////////////////////////////////
				if(player1.muki==player1.LEFT){
					map[player1.x+1][player1.y]=3;//�ʂ����ꏊ�̃}�b�v��3��
				}
				else if(player1.muki==player1.RIGHT){
					map[player1.x-1][player1.y]=3;//�ʂ����ꏊ�̃}�b�v��3��
				}
				else if(player1.muki==player1.UP){
					map[player1.x][player1.y+1]=3;//�ʂ����ꏊ�̃}�b�v��3��
				}
				else if(player1.muki==player1.DOWN){
					map[player1.x][player1.y-1]=3;//�ʂ����ꏊ�̃}�b�v��3��
				}
				map[player1.x][player1.y]=3;//�ʂ����ꏊ�̃}�b�v��3��
				kiri_sound.play();//�؂��艹
			}////////////////////////////////////////////////////////////////////////////////////////////////////
			
			
			
			switch(cd){
			case KeyEvent.VK_LEFT:              // (���@�ړ�left)
				if(player1.x==1){break;}
				if(player1.muki!=player1.LEFT){//�������ς������@�Ȃ���p���L��
					idou[magari][0]=player1.x;
					idou[magari][1]=player1.y;
					magari++;//���Ȃ��������Ƃɂ���
				}
				
				
				if(ctrl==true){
					if(player1.kiridasi==false){
						if(map[player1.x-2][player1.y]==0){//�}�b�v�ɂ����Ė��J��̈�ł����
							map[player1.x][player1.y]=3;//�o���_�̃}�b�v��3��
							player1.kiridasi=true;
							if (player1.x >= 2) player1.x-=2;
							map[player1.x][player1.y]=3;//�ʂ����ꏊ�̃}�b�v��3��
							
							player1.muki=player1.LEFT;//���݂̌��������֐ݒ�
							player1.start_muki=player1.LEFT;//�؂�o���������������֐ݒ�
						}
					}
					else if(player1.kiridasi==true){
						if(map[player1.x-2][player1.y]==0){//�}�b�v�ɂ����Ė��J��̈�ł����
							if (player1.x >= 2) player1.x-=2;
							map[player1.x][player1.y]=3;//�ʂ����ꏊ�̃}�b�v��3��
							player1.muki=player1.LEFT;
						}
						else if(map[player1.x-2][player1.y]==2){//�؂���@�����̂Ƃ�
							if (player1.x >= 2) player1.x-=2;
							map[player1.x][player1.y]=map[player1.x+1][player1.y]=3;//�ʂ����ꏊ�̃}�b�v��3��
							player1.muki=player1.LEFT;
							player1.end_muki=player1.LEFT;//�؂�o���I�����������֐ݒ�
							idou[magari][0]=player1.x;
							idou[magari][1]=player1.y;
							magari++;//���Ȃ��������Ƃɂ���
							kakomi.kakomi();
						}
					}
				}
				else{
					if(player1.kiridasi==false){
						if(map[player1.x-2][player1.y]==2){//�}�b�v�ɂ����Ĉړ��\�̈�ł����
							player1.x-=2;
							player1.muki=player1.LEFT;
						}
					}
					else if(player1.kiridasi==true){
						if(map[player1.x-2][player1.y]==0){//�}�b�v�ɂ����Ė��J��̈�ł����
							map[player1.x][player1.y]=3;//�ʂ����ꏊ�̃}�b�v��3��
							if (player1.x >= 2) player1.x-=2;
							player1.muki=player1.LEFT;
						}
						else if(map[player1.x-2][player1.y]==2){//�؂���@�����̂Ƃ�
							if (player1.x >= 2) player1.x-=2;
							map[player1.x][player1.y]=map[player1.x+1][player1.y]=3;//�ʂ����ꏊ�̃}�b�v��3��
							player1.muki=player1.LEFT;
							player1.end_muki=player1.LEFT;//�؂�o���I�����������֐ݒ�
							idou[magari][0]=player1.x;
							idou[magari][1]=player1.y;
							magari++;//���Ȃ��������Ƃɂ���
							
							kakomi.kakomi();
						}
					}
				}
				break;
			case KeyEvent.VK_RIGHT:				// (���@�ړ�right)
				if(player1.x==637){break;}
				if(player1.muki!=player1.RIGHT){
					idou[magari][0]=player1.x;
					idou[magari][1]=player1.y;
					magari++;//���Ȃ��������Ƃɂ���
				}

				if(ctrl==true){
					if(player1.kiridasi==false){
						if(map[player1.x+2][player1.y]==0){//�}�b�v�ɂ����Ė��J��̈�ł����
							map[player1.x][player1.y]=3;//�o���_�̃}�b�v��3��
							player1.kiridasi=true;
							if (player1.x <= 639) player1.x+=2;
							player1.muki=player1.RIGHT;
							player1.start_muki=player1.RIGHT;//�؂�o���J�n�������E�֐ݒ�
						}
					}
					else if(player1.kiridasi==true){
						if(map[player1.x+2][player1.y]==0){//�}�b�v�ɂ����Ė��J��̈�ł����
							if (player1.x <= 639) player1.x+=2;
							player1.muki=player1.RIGHT;
						}
						else if(map[player1.x+2][player1.y]==2){//�؂���@�����̂Ƃ�
							if (player1.x <= 639) player1.x+=2;
							map[player1.x][player1.y]=map[player1.x-1][player1.y]=3;//�ʂ����ꏊ�̃}�b�v��3��
							player1.muki=player1.RIGHT;
							player1.end_muki=player1.RIGHT;//�؂�o���I���������E�֐ݒ�
							idou[magari][0]=player1.x;
							idou[magari][1]=player1.y;
							magari++;//���Ȃ��������Ƃɂ���
							
							kakomi.kakomi();
						}
					}
				}
				else{
					if(player1.kiridasi==false){
						if(map[player1.x+2][player1.y]==2){//�}�b�v�ɂ����Ĉړ��\�̈�ł����
							player1.x+=2;
							player1.muki=player1.RIGHT;
						}
					}
					else if(player1.kiridasi==true){
						if(map[player1.x+2][player1.y]==0){//�}�b�v�ɂ����Ė��J��̈�ł����
							if (player1.x <= 639) player1.x+=2;
							player1.muki=player1.RIGHT;
						}
						else if(map[player1.x+2][player1.y]==2){//�؂���@�����̂Ƃ�
							if (player1.x <= 639) player1.x+=2;
							map[player1.x][player1.y]=map[player1.x-1][player1.y]=3;//�ʂ����ꏊ�̃}�b�v��3��
							player1.muki=player1.RIGHT;
							player1.end_muki=player1.RIGHT;//�؂�o���I���������E�֐ݒ�
							idou[magari][0]=player1.x;
							idou[magari][1]=player1.y;
							magari++;//���Ȃ��������Ƃɂ���
							
							kakomi.kakomi();
						}
					}
				}
				break;
			case KeyEvent.VK_UP:				// (���@�ړ�up)
				if(player1.y==1){break;}
				if(player1.muki!=player1.UP){
					idou[magari][0]=player1.x;
					idou[magari][1]=player1.y;
					magari++;//���Ȃ��������Ƃɂ���
				}

				if(ctrl==true){
					if(player1.kiridasi==false){
						if(map[player1.x][player1.y-2]==0){//�}�b�v�ɂ����Ė��J��̈�ł����
							map[player1.x][player1.y]=3;//�o���_�̃}�b�v��3��
							player1.kiridasi=true;
							if (player1.y >= 2) player1.y-=2;
							player1.muki=player1.UP;
							player1.start_muki=player1.UP;//�؂�o���J�n��������֐ݒ�
						}
					}
					else if(player1.kiridasi==true){
						if(map[player1.x][player1.y-2]==0){//�}�b�v�ɂ����Ė��J��̈�ł����
							if (player1.y >= 2) player1.y-=2;
							player1.muki=player1.UP;
						}
						else if(map[player1.x][player1.y-2]==2){//�؂���@�����̂Ƃ�
							if (player1.y >=2) player1.y-=2;
							map[player1.x][player1.y]=map[player1.x][player1.y+1]=3;//�ʂ����ꏊ�̃}�b�v��3��
							player1.muki=player1.UP;
							player1.end_muki=player1.UP;//�؂�o���I����������֐ݒ�
							idou[magari][0]=player1.x;
							idou[magari][1]=player1.y;
							magari++;//���Ȃ��������Ƃɂ���
							
							kakomi.kakomi();
						}
					}
				}
				else{
					if(player1.kiridasi==false){
						if(map[player1.x][player1.y-2]==2){//�}�b�v�ɂ����Ĉړ��\�̈�ł����
							player1.y-=2;
							player1.muki=player1.UP;
						}
					}
					else if(player1.kiridasi==true){
						if(map[player1.x][player1.y-2]==0){//�}�b�v�ɂ����Ė��J��̈�ł����
							if (player1.y >= 2) player1.y-=2;
							player1.muki=player1.UP;
						}
						else if(map[player1.x][player1.y-2]==2){//�؂���@�����̂Ƃ�
							if (player1.y >=2) player1.y-=2;
							map[player1.x][player1.y]=map[player1.x][player1.y+1]=3;//�ʂ����ꏊ�̃}�b�v��3��
							player1.muki=player1.UP;
							player1.end_muki=player1.UP;//�؂�o���I����������֐ݒ�
							idou[magari][0]=player1.x;
							idou[magari][1]=player1.y;
							magari++;//���Ȃ��������Ƃɂ���
							
							kakomi.kakomi();
						}
					}
				}
				break;
			case KeyEvent.VK_DOWN:				// (���@�ړ�down)
				if(player1.x==437){break;}
				if(player1.muki!=player1.DOWN){
					idou[magari][0]=player1.x;
					idou[magari][1]=player1.y;
					magari++;//���Ȃ��������Ƃɂ���
				}

				if(ctrl==true){
					if(player1.kiridasi==false){
						if(map[player1.x][player1.y+2]==0){//�}�b�v�ɂ����Ė��J��̈�ł����
							map[player1.x][player1.y]=3;//�o���_�̃}�b�v��3��
							player1.kiridasi=true;
							if (player1.y <= 479) player1.y+=2;
							player1.muki=player1.DOWN;
							player1.start_muki=player1.DOWN;//�؂�o���J�n���������֐ݒ�
						}
					}
					else if(player1.kiridasi==true){
						if(map[player1.x][player1.y+2]==0){//�}�b�v�ɂ����Ė��J��̈�ł����
							if (player1.y <= 479) player1.y+=2;
							player1.muki=player1.DOWN;
						}
						else if(map[player1.x][player1.y+2]==2){//�؂���@�����̂Ƃ�
							if (player1.y <= 479) player1.y+=2;
							map[player1.x][player1.y]=map[player1.x][player1.y-1]=3;//�ʂ����ꏊ�̃}�b�v��3��
							player1.muki=player1.DOWN;
							player1.end_muki=player1.DOWN;//�؂�o���I�����������֐ݒ�
							idou[magari][0]=player1.x;
							idou[magari][1]=player1.y;
							magari++;//���Ȃ��������Ƃɂ���
							
							kakomi.kakomi();
						}
					}
				}
				else{
					if(player1.kiridasi==false){
						if(map[player1.x][player1.y+2]==2){//�}�b�v�ɂ����Ĉړ��\�̈�ł����
							player1.y+=2;
							player1.muki=player1.DOWN;
						}
					}
					else if(player1.kiridasi==true){
						if(map[player1.x][player1.y+2]==0){//�}�b�v�ɂ����Ė��J��̈�ł����
							if (player1.y <= 479) player1.y+=2;
							player1.muki=player1.DOWN;
						}
						else if(map[player1.x][player1.y+2]==2){//�؂���@�����̂Ƃ�
							if (player1.y <= 479) player1.y+=2;
							map[player1.x][player1.y]=map[player1.x][player1.y-1]=3;//�ʂ����ꏊ�̃}�b�v��3��
							player1.muki=player1.DOWN;
							player1.end_muki=player1.DOWN;//�؂�o���I�����������֐ݒ�
							idou[magari][0]=player1.x;
							idou[magari][1]=player1.y;
							magari++;//���Ȃ��������Ƃɂ���
							
							kakomi.kakomi();
						}
					}
				}
				break;
			}
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////
	//�L�[�𗣂���/////////////////////////////////////////////////////////////////////////////////////
	public void keyReleased(KeyEvent e){//�L�[�������ꂽ��
		;//�����Ȃ�
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	//���C���������[�v�֐�///////////////////////////////////////////////////////////////////////////
	public void run(){
		start_sound.play();//�X�^�[�g����炷
			
		while (true){//�������[�v
			try{
				
				repaint();//�`��
				Thread.sleep(1000/FRAME_LATE);//�����x�����ԁ@�܂�A�t���[�����[�g�̐ݒ�B
				
			}
			catch(Exception e){}//�K���ɃG���[���m
		}
		
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void pause(){//�X���b�h��~���O������Ăяo������
		enemy_thread.pause();
	}
	public void contine(){//�X���b�h�ĊJ���O������w�����邽��
		enemy_thread.contine();
	}
	
	
	//������h�~�̂��ߕ`��A�b�v�f�[�g�֐����I�[�o�[���[�h���ĕ`��N���A�[�������͂Ԃ��Ă���/////////
	public void update(Graphics g){
		paint(g);//�`��
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////
	//************************************************************************************************
	
	public void gameover(){//�Q�[���I�[�o�[
			if(gameover_flag==false){
				gameover_sound.play();//�Q�[���I�[�o�[�̉���炷
				gameover_flag=true;//�Q�[���I�[�o�[�t���OON
			}
			backbuf.drawString(gameover_massage,32,200);
			backbuf.drawString("�����{�^���������Ă�������",32,280+font);
			backbuf.drawString("���X�^�[�g���܂��B",32,280+font+font);
			gameplay=false;
			time_thread.pause();
			enemy_thread.pause();
	}
	
	public void gameclear(){//�Q�[���N���A�[
		if(clear_gazou==false){
				if(clear==false){
					clear=true;//�N���A�[�t���OON
					stageclear_sound.play();//�X�e�[�W�N���A�[�̉���炷
				}
				
				for (int i=0;i<teki_kazu;i++){
					teki[i].dead = true;
				}
					
				backbuf.drawString(stage_clear_massage,200,200);
				backbuf.drawString("�����{�^���������Ă��������B",200,200+font);
			}
			else if(clear_gazou==true){
				backbuf.drawImage(backcg,0,0,null);//�w�i�摜�̕\��
			}
			gameplay=false;
			if(time_infinity==false){
				time_thread.pause();//���Ԓ�~
			}
			enemy_thread.pause();//�G�X���b�h����~
	}
	
	//��ʕ`��֐�//////////////////////////////////////////////////////////////////////////////////////
	public void paint(Graphics g){
		backbuf.drawImage(back_surface2,0,0,this);
		if(player1.kiridasi==true){
			backbuf.setColor(Color.green);//�J���[��΂ɃZ�b�g
			backbuf.drawImage(player1.gazou_kiri,(player1.x-((int)player1.kiri_width/2)),(player1.y-((int)player1.kiri_height/2)),this);//���@�̕\��
			for(i=0;i<magari-1;i++){//�؂�����̕\��
				backbuf.drawLine(idou[i][0],idou[i][1],idou[i+1][0],idou[i+1][1]);
			}
			backbuf.drawLine(idou[magari-1][0],idou[magari-1][1],player1.x,player1.y);//�Ō�̋Ȃ���p���猻�ݒn�܂Ő�����
		}
		else if(player1.kiridasi==false){
			backbuf.drawImage(player1.gazou,(player1.x-((player1.width)/2)),(player1.y-((player1.height)/2)),this);//���@�̕\��
		}
		
		for(int i=0;i<teki_kazu;i++){
			if(teki[i].dead==false){
				backbuf.drawImage(teki[i].gazou,teki[i].x,teki[i].y,this);//�G�̕\��
			}
		}
		
		backbuf.setColor(Color.white);//�J���[�𔒂ɃZ�b�g
		if(color!=null){
			try{
				backbuf.setColor(new Color( Integer.decode("0x" + color.substring(0,2)).intValue(),
											Integer.decode("0x" + color.substring(2,4)).intValue(),
											Integer.decode("0x" + color.substring(4,6)).intValue() ));
			}catch(Exception e){
			}
		}
		if(time_infinity==false){//���Ԑ����t���O����
			if(time_hurry==true){
				backbuf.setColor(Color.red);
			}
			backbuf.drawString("���@�c�ʁF" + player1.kisuu + "�@�@�J��̈�F" + Math.round(parsent)
							   + "%�@�@�������ԁF" + main_time  +  "�b",10,475);//�c�@�A�J��̈�A���Ԃ̕\��
		}
		else if(time_infinity==true){//���Ԑ����t���O�Ȃ�
			backbuf.drawString("���@�c�ʁF" + player1.kisuu + "�@�@�J��̈�F" + Math.round(parsent)
							   + "%",10,475);//�c�@�A�J��̈�
		}	
		
		
		if(main_time<0 || player1.kisuu<=0){//�Q�[���I�[�o�[�Ȃ��
			gameover();
		}
		
		if(Math.round(parsent)>=clear_menseki){//�Q�[���N���A�[�Ȃ��
			gameclear();
		}
		
		g.drawImage(pri_surface,0,0,this);//���̃�������摜���@�\(�{���̉�ʁj�֓]��
	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////
}

//���ԊǗ��֐�//////////////////////////////////////////////////////////////////////////////////////
class time_manager implements Runnable{
	public quicks quicks = null;
	private volatile Thread blinker;//�X���b�h�����p
	private boolean threadsuspended = false;//�T�X�y���h�p

	public time_manager(quicks q){//�R���X�g���N�^�@Quicks�N���X����̎Q�Ɠn��
		this.quicks = q;
	}
	public void start(){//�O������myth.start()���Ăяo���A�X���b�h���N���ł���
		blinker = new Thread(this);
        blinker.start();
	}
	public void stop(){
		//blinker = null;
        //notify();
		blinker.stop();//�X���b�h�j��
	}
	public void contine(){//�ĊJ
        threadsuspended = false;
        //notify();
		//myth.contine();
	}
	public void pause(){//��~
		threadsuspended = true;
		//myth.pause();
	}
	
	public void setPriority(int obj){
		blinker.setPriority(obj);
	}
	
	public boolean isAlive(){
		if(blinker.isAlive()==true){//�����Ă�����
			//myth.destroy();//�j��
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
					if(this.quicks.time_hurry==false){//���Ԑ������낻��⃔�����t���O���܂������ĂȂ���Ԃ�
						if(this.quicks.main_time<60){//�������Ԃ��c��60�b��؂�����
							this.quicks.time_hurry=true;//���Ԑ������낻��⃔�����t���O�𗧂Ă�
						}
					}
					this.quicks.main_time--;//���Ԃ���b���炷
				}
				Thread.sleep(1000);//��b�҂�
			}
			catch(Exception e){}//�K���ɃG���[���m
		}
	}
}
	////////////////////////////////////////////////////////////////////////////////////////////////////

class enemy_manager implements Runnable{
	public quicks quicks = null;//Quicks�N���X�̃C���X�^���X���Q�Ɠn�����邽�߂Ɏg��
	private volatile Thread blinker;//�X���b�h�����p
	private boolean threadsuspended = false;//�T�X�y���h�p

	public enemy_manager(quicks q){
		this.quicks = q;//Quicks�N���X����̃C���X�^���X���Q�Ɠn��
	}
	public void start(){//�O������myth.start()���Ăяo���A�X���b�h���N���ł���
		blinker = new Thread(this);
        blinker.start();
	}
	public void stop(){
		//blinker = null;
        //notify();
		blinker.stop();//�X���b�h�j��
	}
	public void contine(){//�ĊJ
		threadsuspended = false;
        //notify();
		//myth.contine();
	}
	public void pause(){//��~
		threadsuspended = true;
		//myth.pause();
	}	
	public void setPriority(int obj){
		blinker.setPriority(obj);
	}
	
	public boolean isAlive(){
		if(blinker.isAlive()==true){//�����Ă�����
			//myth.destroy();//�j��
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
					
					Thread.sleep(1000/this.quicks.FRAME_LATE);//�����x�����ԁ@�܂�A�t���[�����[�g�̐ݒ�B
			}
			catch(Exception e){}//�K���ɃG���[���m
		}
	}
}
	////////////////////////////////////////////////////////////////////////////////////////////////////



//�͂ݔ��菈���N���X/////////////////////////////////////////////////////////////////////////////////
class kakomi_hantei{
	public quicks quicks= null;//Quicks�N���X����̃C���X�^���X���Q�Ɠn��
	final int UP=5,DOWN=6,RIGHT=7,LEFT=8;//�㉺���E���ȒP�ȏ����Ő���ł���悤�ɂ��Ă���
	int i,i2,j;
	double par;//�J�E���^�A�؂������̕ێ�
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
	int linepart_x=0;//���������l�p�`�̉��̐�
	int linepart_y=0;//���������l�p�`�̏c�̐�
	int x_num=0,y_num=0;//x_line,y_line�z��̒��g���J�E���g���邽��
	int x_line[] = new int[30000];//�؂������x���W������
	int y_line[] = new int[30000];//�؂������y���W������
	int temp_line[][] = new int[50000][2];
	int magattaKazu = 0;
	int magattaKazu1 = 0;
	int firstflag=0;
	boolean hantei=false;
	Stack stack = new Stack();
	int fillrectflag[] = new int[100000];
	int rect_num;
	
	byte kiri_num=3;//�؂�����
	int point;//�؂�Ԃ����_�̐�
	
	public kakomi_hantei(quicks q){
		this.quicks = q;
	}
	
	public void kakomi(){
		
		if(this.quicks.time_infinity==false){//�^�C���X���b�h�ꎞ��~
			quicks.time_thread.pause();
		}
		quicks.enemy_thread.pause();
		
		a=quicks.magari;
		for(i=0;i<a;i++){
			if(temp_max_x<quicks.idou[i][0]){//x���W�̍ő�l�����߂�
				temp_max_x=quicks.idou[i][0];
			}
			if(temp_max_y<quicks.idou[i][1]){//y���W�̍ő�l�����߂�
				temp_max_y=quicks.idou[i][1];
			}
			if(temp_min_x>quicks.idou[i][0]){//x���W�̍ŏ��l�����߂�
				temp_min_x=quicks.idou[i][0];
			}
			if(temp_min_y>quicks.idou[i][1]){//y���W�̍ŏ��l�����߂�
				temp_min_y=quicks.idou[i][1];
			}
		}

		//�؂�o���I���_�̕ێ�
		sarchar1_x=quicks.idou[quicks.magari-1][0];
		sarchar1_y=quicks.idou[quicks.magari-1][1];
		sarchar2_x=quicks.idou[quicks.magari-1][0];
		sarchar2_y=quicks.idou[quicks.magari-1][1];		
		
		//�͂݊֐��������������_�ł̎�l���̌���
		sarchmuki2=sarchmuki1=quicks.player1.end_muki;
		nagasa1=nagasa2=0;
		
		//�͂ނׂ����p�`�̑S�p�_�����߂�

		
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
					}else if(quicks.map[sarchar1_x][sarchar1_y-1]==2){//�܂����T��
						nagasa1++;
						sarchar1_y=sarchar1_y-1;
					}else if(quicks.map[sarchar1_x-1][sarchar1_y]==2){//�����č�
						sarchmuki1=LEFT;
						break;
					}else if(quicks.map[sarchar1_x+1][sarchar1_y]==2){//����ł��Ȃ���΍��x�͉E
						sarchmuki1=RIGHT;
						break;
					}else if(quicks.idou[0][0]==sarchar1_x&&quicks.idou[0][1]==sarchar1_y-1){//����ł��Ȃ��Ƃ���ƁA���łɓ������Ă邩������Ȃ��̂Ń`�F�b�N
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
		
		
		
		
		
		
		if(nagasa1<nagasa2){//��̒T�������ł�苗�����Z�������ق������B
			flag1=true;
		}else{
			flag1=false;
		}
		
		cheakmax_x=temp_max_x;
		cheakmax_y=temp_max_y;
		cheakmin_x=temp_min_x;
		cheakmin_y=temp_min_y;
		
		//�J�n�ƏI���̕��������߂�B
		startmuki=quicks.player1.start_muki;
		endmuki=quicks.player1.end_muki;
		//���x�����O���邽�߂̏����t���O�i"1"�̂��Ɓj���P���Ă�
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
		
		if(flag1==true){//�Ȃ���p���W�̒��ōł��傫�ȍ��W�A�ł������ȍ��W�𔲂��o���B
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
		
		fillRects();//���x�����O�@�ɂ��@�h��Ԃ�
		fillLine();//�͂܂ꂽ�����̋����E��������
		
		for(i=0;i<640;i++){
			for(i2=0;i2<480;i2++){
				if(quicks.map[i][i2]==3){//�؂��蒆�ɒʂ����O��
					quicks.map[i][i2]=2;
				}
			}
		}
		

		quicks.tempx_max=cheakmax_x;
		quicks.tempy_max=cheakmax_y;
		quicks.tempx_min=cheakmin_x;
		quicks.tempy_min=cheakmin_y;
		this.Back();//�`�揈���o�b�N�������̈�̍X�V �֐��̌Ăяo��
		quicks.player1.kiridasi=false;//�؂�o���t���O���@OFF��
		
		temp_min_x=637;//�؂���l�p�`���W����ϐ��̏�����
		temp_min_y=477;
		temp_max_x=1;
		temp_max_y=1;

		//�X���b�h�ĊJ
		if(quicks.time_infinity==false){
			quicks.time_thread.contine();
		}
		quicks.enemy_thread.contine();
		
	}
	
	//���C���h��Ԃ����\�b�h--------------------------------------------------------------------------------
	public void fillLine(){
		int i=0;
		int j=0;
		if(flag1==true){
			for(i=0;i<po1;i++){
				if(cheak1[i][0]==cheak1[i+1][0]){//x�̗񂪓�����
					if(cheak1[i][1]>cheak1[i+1][1]){
						for(j=cheak1[i+1][1];j<=cheak1[i][1];j++){
							quicks.map[cheak1[i][0]][j]=1;
						}
					}else{
						for(j=cheak1[i][1];j<=cheak1[i+1][1];j++){
							quicks.map[cheak1[i][0]][j]=1;
						}
					}
				}else if(cheak1[i][1]==cheak1[i+1][1]){//y�̗񂪓�����
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
				if(cheak2[i][0]==cheak2[i+1][0]){//x�̗񂪓�����
					if(cheak2[i][1]>cheak2[i+1][1]){
						for(j=cheak2[i+1][1];j<=cheak2[i][1];j++){
							quicks.map[cheak2[i][0]][j]=1;
						}
					}else{
						for(j=cheak2[i][1];j<=cheak2[i+1][1];j++){
							quicks.map[cheak2[i][0]][j]=1;
						}
					}
				}else if(cheak2[i][1]==cheak2[i+1][1]){//y�̗񂪓�����
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
		quicks.map[cheak1[po1][0]][cheak1[po1][1]]=2;//�����_���ēx���E���u�Q�v��
		quicks.map[cheak1[0][0]][cheak1[0][1]]=2;//�o���_���ēx�E�E�E
	}
	
	//���C���h��Ԃ����\�b�h--------------------------------------------------------------------------------
	public void fillLine(int cheak[][],int po1){
		int i=0;
		int j=0;
		if(flag1==true){
			for(i=0;i<po1;i++){
				if(cheak[i][0]==cheak[i+1][0]){//x�̗񂪓�����
					if(cheak[i][1]>cheak[i+1][1]){
						for(j=cheak[i+1][1];j<=cheak[i][1];j++){
							quicks.map[cheak[i][0]][j]=2;
						}
					}else{
						for(j=cheak[i][1];j<=cheak[i+1][1];j++){
							quicks.map[cheak[i][0]][j]=2;
						}
					}
				}else if(cheak[i][1]==cheak[i+1][1]){//y�̗񂪓�����
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
		quicks.map[cheak[po1][0]][cheak[po1][1]]=2;//�����_���ēx���E���u�Q�v��
		quicks.map[cheak[0][0]][cheak[0][1]]=2;//�o���_���ēx�E�E�E
	}
	
	//���C���h��Ԃ����\�b�h--------------------------------------------------------------------------------
	public void fillLine(int cheak[][],int po1,int a){
		int i=0;
		int j=0;
		if(flag1==true){
			for(i=0;i<po1;i++){
				if(cheak[i][0]==cheak[i+1][0]){//x�̗񂪓�����
					if(cheak[i][1]>cheak[i+1][1]){
						for(j=cheak[i+1][1];j<=cheak[i][1];j++){
							quicks.map[cheak[i][0]][j]=2;
						}
					}else{
						for(j=cheak[i][1];j<=cheak[i+1][1];j++){
							quicks.map[cheak[i][0]][j]=2;
						}
					}
				}else if(cheak[i][1]==cheak[i+1][1]){//y�̗񂪓�����
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
		quicks.map[cheak[po1][0]][cheak[po1][1]]=2;//�����_���ēx���E���u�Q�v��
		quicks.map[cheak[0][0]][cheak[0][1]]=2;//�o���_���ēx�E�E�E
	}
	
	//1�t���O���ēh��Ԃ��������\�b�h------------------------------------------------------------------------
	public void fillRects(){
		int i,j;
		
		if(flag1==true){
			//�܂��A���̈�̋Ȃ���p�̍��W���L�^����
			for(i=0;i<po1;i++){
				x_line[i]=temp_line[i][0]=cheak1[i][0];
				y_line[i]=temp_line[i][1]=cheak1[i][1];
			}
			j=i;	//x_line,y_line�Ƀf�[�^�����Ԃɕۑ����邽�߂�i�̒l��ۑ�����
			//����A�؂������Ȃ���p�̍��W���L�^����
			magattaKazu1=i;
			for(i=0;i<quicks.magari-1;i++){
				x_line[i+j]=quicks.idou[i+1][0];
				y_line[i+j]=quicks.idou[i+1][1];
			}
		}else{
			//�܂��A���̈�̋Ȃ���p�̍��W���L�^����
			for(i=0;i<po2;i++){
				x_line[i]=temp_line[i][0]=cheak2[i][0];
				y_line[i]=temp_line[i][1]=cheak2[i][1];
			}
			j=i;	//x_line,y_line�Ƀf�[�^�����Ԃɕۑ����邽�߂�i�̒l��ۑ�����
			//����A�؂������Ȃ���p�̍��W���L�^����
			magattaKazu1=i;
			for(i=0;i<quicks.magari-1;i++){
				x_line[i+j]=quicks.idou[i+1][0];
				y_line[i+j]=quicks.idou[i+1][1];
			}
		}
		
		magattaKazu = i+j;
		
		heapsort(x_line,i+j);//�q�[�v�\�[�g
		heapsort(y_line,i+j);
		
		x_num=ssort_d(x_line,i+j);//�_�u���Ă��鐔�l���폜�Ax_num�ɂ�x_line�z��̔z�񒷂�
		y_num=ssort_d(y_line,i+j);//�_�u���Ă��鐔�l���폜�Ay_num�ɂ�y_line�z��̔z�񒷂�
		
		//System.out.println("���_���W");
		for(i = 0;i<x_num;i++){
			//System.out.println("x" +i + ":" + x_line[i]);
		}
		
		for(i = 0;i<y_num;i++){
			//System.out.println("y" + i + ":" + y_line[i]);
		}
		
		//�����������I�I
		
		linepart_x=x_num-1;//���̎l�p�̐�
		linepart_y=y_num-1;//�c�̎l�p�̐�
		
		rect_num = linepart_x * linepart_y;//�l�p�`�̐�
		//System.out.println("rect_num : " + rect_num);//�f�o�b�N
		
		
		
		for(i=1;i<x_num;i++){
			if(fillrectflag_x<x_line[i]){
				for(j=1;j<y_num;j++){
					if(fillrectflag_y<y_line[j]){
						firstflag = (j-1)*linepart_x + (i-1);
						fillrectflag[firstflag]=1;
						//System.out.println(firstflag);//�f�o�b�N
						
						break;
					}	
				}
				break;
			}
		}
		
		
		cheak_border(firstflag);

		for(i=0;i<rect_num;i++){
			if(fillrectflag[i]==1){
				//System.out.println("------------------\nfillrect No." + i);//�f�o�b�N
				fillpartRects(getrect_x(i,1),getrect_y(i,1),getrect_x(i,2),getrect_y(i,2));
			}
		}
		
		//fillLine(temp_line,magattaKazu);
		
		//fillLine(temp_line,magattaKazu1,1);
		
	}
	
	
	//�S�A���A�����胁�\�b�h--------------------------------------------------------------------------
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
	
	//�������ꂽ�l�p�`�h��Ԃ����\�b�h----------------------------------------------------------------------
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
	
	//�w�肳�ꂽ�l�p�`�̒��_�̂����W��Ԃ����\�b�h--------------------------------------------------------------
	//rect_No�ɂ͎l�p�`�̔ԍ���,data�ɂ͎��ʗp������1�Ȃ獶���̍��W�A2�Ȃ�E���̍��W��Ԃ�
	
	int getrect_x(int rect_No,int data){
		
		int ab_rect_No;		//��Βl�̎l�p�`�ԍ����i�[����ϐ�
		
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
	
	//�w�肳�ꂽ�l�p�`�̒��_�̂����W��Ԃ����\�b�h---------------------------------------------------------------
	//rect_No�ɂ͎l�p�`�̔ԍ���,data�ɂ͎��ʗp������1�Ȃ�㑤�̍��W�A2�Ȃ牺���̍��W��Ԃ�
	
	int getrect_y(int rect_No,int data){
		
		int ab_rect_No;		//��Βl�̎l�p�`�ԍ����i�[����ϐ�
		
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
	
	
	//�`�揈���o�b�N�������̈�̍X�V---------------------------------------------------------------------------
	private void Back(){
		quicks.kakomi_sound.play();//�͂݊����̉���炷
		
		this.quicks.backbuf3.drawImage(this.quicks.back_surface2,0,0,null);//�O��L���w�i�摜�̕\��

		for(i=0;i<=rect_num;i++){
			drawPartRects(getrect_x(i,1),getrect_y(i,1),getrect_x(i,2),getrect_y(i,2),i);
		}
		this.quicks.backbuf3.setColor(Color.white);//�J���[�𔒂ɃZ�b�g
		for(i=0;i<=quicks.magari-2;i++){
			this.quicks.backbuf3.drawLine(quicks.idou[i][0],quicks.idou[i][1],quicks.idou[i+1][0],quicks.idou[i+1][1]);
		}
		
		//������
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
		
		this.quicks.backbuf2.drawImage(this.quicks.back_surface2,0,0,null);//�o�����G�𗠃o�b�t�@�֓]��
		
		quicks.parsent=(par/290000.0)*100.0;//�����؂��������v���o��
			}//-------------------------------------------------------------------------------------------------------
	
	
	//�`�悷��------------------------------------------------------------------------------------------------
	private void drawPartRects(int min_x,int min_y,int max_x,int max_y,int z){
		if(fillrectflag[z]==1){//�t���O�������Ă���l�p�`�̈�ł���Γh��
			this.quicks.backbuf3.drawImage(this.quicks.backcg,min_x,min_y,max_x+1,max_y+1,min_x,min_y,max_x+1,max_y+1,null);
			par+=((max_x - min_x) * (max_y - min_y));//�ʐϕ��@�͂ݗ̈恓�֒ǉ�
		}
	}//--------------------------------------------------------------------------------------------------------
	
	////////////// �q�[�v�\�[�g//////////////////////////////////////////////////////////////////
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
	
	//------���`�\�[�g�i�_�u���Ă��鐔�l���폜�A�z����l�߂�j---------------------------------------------
	
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


//�v���C���[����N���X////////////////////////////////////////////////////////////////////////////
class player{
	final int UP=5,DOWN=6,RIGHT=7,LEFT=8;//�㉺���E���ȒP�ȏ����Ő���ł���悤�ɂ��Ă���
	public int x,y;//�ʒu���
	public int width=10,height=10;//�摜�̕��A�����@���
	public int kiri_width=10,kiri_height=10;//�؂��蒆�摜�̕��A�����@���
	public int muki;//�������
	public int start_muki;//�؂�o����������
	public int end_muki;//�؂�o���I������
	public Image gazou;//�摜���
	public Image gazou_kiri;//�؂�o�����̉摜
	public boolean kiridasi=false;//�؂�o���t���O
	public int kisuu=5;//�c��@�̂̐�
	public int sokudo=2;//�v���C���[�̈ړ����x
	
	player(){//�R���X�g���N�^�@�����ʒu�̌���
		this.x=1;
		this.y=1;
	}
	player(int X,int Y){//�����ʒu���w�肷�邱�Ƃ��ł���B
		this.x=X;
		this.y=Y;
	}
}
/////////////////////////////////////////////////////////////////////////////////////////////////
//�G����N���X///////////////////////////////////////////////////////////////////////////////////
class enemy implements Runnable{
	public quicks quicks = null;//Quicks�N���X����̎Q�Ɠn��
	public int x,y;//�ʒu���
	public int muki_x;//�������
	public int muki_y;
	public int width=1;//����
	public int height=1;//�c��
	public int sokudo;//�G�̈ړ����x
	public int logic;//CPU �A���S���Y���@�s���p�^�[�����œ��삷�邩�H
	public boolean dead=false;//���S�t���O
	public Image gazou;//�G�@�摜�ǂݍ��ݗp
	public int i;//temp�p
	enemy(quicks q){//�R���X�g���N�^�@�����ʒu�̌���
		this.quicks = q;//Quicks�N���X����̎Q�Ɠn��
		this.x=(int)(Math.random() * (double)400);//�����_���ʒu
		this.y=(int)(Math.random() * (double)400);//�����_���ʒu
		logic=(int)((Math.random() * (double)4)+1);
		sokudo=2;
		if(logic<2){muki_x=muki_y=1;}
		if(logic==2){muki_x=1;muki_y=-1;}
		if(logic==3){muki_x=muki_y=-1;}
		if(logic>3){muki_x=-1;muki_y=1;}
	}
	enemy(quicks q,int X,int Y,int LOGIC,int SOKUDO){//�����ʒu���w�肷�邱�Ƃ��ł���B
		this.quicks = q;//Quicks�N���X����̎Q�Ɠn��
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
					if(dead==false){//�G�������Ă���Ƃ���������@����ł���Ƃ��͂��̏����͔�΂�
						i=sokudo;
						while(i>0){
							i--;
							if(quicks.map[(x+(width/2))][(y+(height/2))]==1){
								dead=true;//���S
								quicks.butukari_sound.play();//�Ԃ��艹����
							}
							else if(quicks.map[x][y]==3||quicks.map[x+width][y]==3||quicks.map[x][y+height]==3||quicks.map[x+width][y+height]==3||
									(quicks.player1.kiridasi==true && !(quicks.player1.x>(this.x+this.width) || quicks.player1.y>(this.y+this.height) || (quicks.player1.x+quicks.player1.width)<this.x || (quicks.player1.y+quicks.player1.height)<this.y ))){//���@�̐؂�����O�ՂɂԂ�������
								quicks.kill.dead();//���S����
							}
							if(muki_x==1){//�E����
								if(quicks.map[x+1+width][y]==2||quicks.map[x+1+width][y+height]==2){//�G�����w�n���E���ɐG�ꂽ�Ƃ��̏���
									muki_x=-1;
								}
								else{
									x++;
								}
							}
							else if(muki_x==-1){//������
								if(quicks.map[x-1][y]==2||quicks.map[x-1][y+height]==2){//�G�����w�n���E���ɐG�ꂽ�Ƃ��̏���
									muki_x=1;
								}
								else{
									x--;
								}
							}
							if(muki_y==1){//������
								if(quicks.map[x][y+1+height]==2||quicks.map[x+width][y+1+height]==2){//�G�����w�n���E���ɐG�ꂽ�Ƃ��̏���
									muki_y=-1;
								}
								else{
									y++;
								}
							}
							else if(muki_y==-1){//�����
								if(quicks.map[x][y-1]==2||quicks.map[x+width][y-1]==2){//�G�����w�n���E���ɐG�ꂽ�Ƃ��̏���
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


//���S�@�����N���X///////////////////////////////////////////////////////////////////////////////////////
class sibou{
	public quicks quicks = null;
	public int i,i2;
	public int temp_max_x=1,temp_max_y=1,temp_min_x=637,temp_min_y=477;
	
	public sibou(quicks q){//�R���X�g���N�^�@Quicks�N���X����̎Q�Ɠn��
		this.quicks = q;
	}
	public void dead(){
		
		quicks.idou[quicks.magari][0]=quicks.player1.x;//���񂾒n�_�̍��W��ێ�
		quicks.idou[quicks.magari][1]=quicks.player1.y;
		quicks.magari++;
		quicks.butukari_sound.play();//�Ԃ���������炷
		quicks.player1.kisuu--;//���@������炷
		quicks.player1.kiridasi=false;//�؂�o���t���O���n�e�e
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
				if(quicks.map[i][i2]==3){//3�ł���΁@(�؂���O�Ղł����)
					quicks.map[i][i2]=0;//���J��̈��
				}
			}
		}
		//�o���_���O�ɂȂ��Ă��܂��Ă���̂Œ����K�v������/////////////////////////////////////
		i=quicks.idou[0][0];
		i2=quicks.idou[0][1];
		quicks.map[i][i2]=2;//�o���_�����@���炽�߂ĂQ�ɖ߂��B/////////////////////////////////
		
		temp_min_x=637;//�؂���l�p�`���W����ϐ��̏�����
		temp_min_y=477;
		temp_max_x=1;
		temp_max_y=1;
		
		quicks.player1.x=quicks.idou[0][0];//�v���C���[���o���_�ɖ߂�
		quicks.player1.y=quicks.idou[0][1];
	}
}////////////////////////////////////////////////////////////////////////////////////////////////////////////
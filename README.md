JavaQuicks
==========

/***********************************************************************************
JAVAクイックス開発SDK 1.4
【製作】池田プロダクション
【開発環境】Eclipse 3.1 
              & JDK1.5.1 Released by SunMicrosystems Company.
【対象環境】Windows98/Me/2000/NT/XP JAVA実行対応IE
【必要最低環境】CPU Pentium2   300 MHz
              　　メモリ       64  MB
【推奨環境】　　CPU Pentium3   500 MHz
　　　　　　　　　メモリ　　　 128 MB
【ファイル名】 quicks_sdk.zip
【前提ソフト】 JAVA 対応 WWW ブラウザ
【転載の可否】 ホームページでの掲載大歓迎です。じゃんじゃん使って下さいませm(_ _)m
【改造・修正】 ソースファイルの改造・修正を行い自由に使用して下さい。
*************************************************************************************/
「クイックス」はかつてTAITOが出していた「Qix」を模したゲームです。
要は壁きり陣地取りゲームで、自機を操作して領地を囲い込み一定以上自領域として囲い込むことで
ゲームクリアーとなります。その際敵に接触すると死亡します。
上手く敵をよけつつ、自領域を囲みこみ、クリアーを目指しましょう。

自機は最初一番左上にいます。矢印キーで　自機の操作。
CTRL押しながら切り出せます。　囲んだ陣地は自領域に・・・
敵を自領域に囲い込むと殺せます。　　ただし、敵に自分、もしくは切り取り中の緑線が触れても死亡します。
全体の85%以上　自領域として囲い込むことでクリアーとなります。
キーボードの[R]を押すとゲームを再起動します。

プレイするには必要な各ファイルをHPに配置して index.htmlを開けばOKです。
ただし、JAVAアプレットは一応アプリケーションなのでファイルのパーティション設定が
しっかり出来ていないと正常に起動しません。注意してください。
必要なパーティション設定は以下の通りです。

+----CG 755 (ゲーム中で使う画像ファイルを入れます。)
 |    |
 |    |
 |    |----back.jpg 644
 |    |----front.jpg 644
 |    |----teki0.jpg 644　(・・・などのように画像ファイルが入っています)
 |
 |
 |---sound 755　(ゲーム中で使う音ファイルを入れます。)
 |      |
 |      |----bgm.wav 644
 |      |----butukari.wav 644
 |      |----gameover.wav 644　（・・・などのように音ファイルが入っています）
 |
 |
 |---next_stage このゲームの階層をそのままこのフォルダに同じように入れると
 |　　　　　　　それが二面になります。
 |
 |
 |-------index.html 644（これがゲームを起動するHTMLファイルです。名前は変えてもOKです）
 |-------quicks.jar 644 プログラム実行に必要な本体ファイルが入っています。
 |
 |-------quicks.java これは自分でソースファイルを改造したい人のために入れているものでゲームのプレイに当たって
 |　　　　　　　　　 ホームページ上に置く必要はありません。ゲームには関係ないです。
 |-------説明書.txt このファイルもゲームには関係ありません。

Windows用FTPソフト「FFFTP」などお使いの方は特に意識することなく
そのままファイルをアップロードすれば勝手にこのパーティション設定になると思います。
FFFTP:http://www2.biglobe.ne.jp/~sota/

画像ファイル、サウンドファイルを置き換えることによって
好きな画像、音でのカスタマイズが出来ます。ご自由に変更して使ってください。
・各ファイルの説明
front.jpg ステージの前面画像　640*480のjpg画像を用意してください。
back.jpg  ステージの背面画像　640*480のjpg画像を用意してください。
player1.gif　　　　   自機の画像です。透過GIF形式になっています。
player1_kiri.gif　    自機が陣地を切り取り中の画像です。
teki0.gif～teki6.gif　敵の画像です。各7体まで。

bgm.wav               BGM　wav形式です。
butukari.wav　　　　　敵にぶつかったときの音、また敵を囲い込んで敵が死ぬときの音
gameover.wav          ゲームオーバー時の音
kakomi.wav　　　　　　自領域を囲い込むことに成功した時の音
kiri.wav　　　　　　　自領域囲い込み中の移動音
quicks.mid　　　　　　BGM　MIDI形式です。
stageclear.wav        ステージクリアー時の音
start.wav　　　　　　 ゲーム開始時のスタート音

デフォルト設定では　自動的にこれらのファイルを読み込みにいき、プログラムを実行するようになっていますが
自分で好きなファイルを使うことも出来ます。
アプレットを実装する index.html　などのHTMLファイル内に次の記述を行ってください。

<APPLET CODE="quicks.class" archive="quicks.jar" width=640 height=480>

<!-- 画像の設定 -->
<PARAM NAME="player1" VALUE="CG/player1.gif"><!-- 自機の画像 -->
<PARAM NAME="player1_kiri" VALUE="CG/player1_kiri.gif"><!-- 自機切り取り中の画像 -->
<PARAM NAME="teki0" VALUE="CG/teki0.gif"><!-- 敵1の画像 -->
<PARAM NAME="teki1" VALUE="CG/teki1.gif"><!-- 敵2の画像 -->
<PARAM NAME="teki2" VALUE="CG/teki2.gif"><!-- 敵3の画像 -->
<PARAM NAME="teki3" VALUE="CG/teki3.gif"><!-- 敵4の画像 -->
<PARAM NAME="teki4" VALUE="CG/teki3.gif"><!-- 敵5の画像 -->
<PARAM NAME="teki5" VALUE="CG/teki2.gif"><!-- 敵6の画像 -->
<PARAM NAME="teki6" VALUE="CG/teki1.gif"><!-- 敵7の画像 -->
<PARAM NAME="front" VALUE="CG/front.jpg"><!-- ステージ前面画像(切る前の画像) -->
<PARAM NAME="back" VALUE="CG/back.jpg"><!-- ステージ背景画像(切ったときに見える画像) -->
<PARAM NAME="gameclear_URL" VALUE="no"><!-- クリアーした後に自動的に飛ぶページのURL、"no"と書いておくと飛ばない -->

<!-- 音の設定 -->
<PARAM NAME="start" VALUE="sound/start.wav"><!-- ゲーム開始の時にならす音 -->
<PARAM NAME="stageclear" VALUE="sound/stageclear.wav"><!-- ゲームクリアーの時にならす音 -->
<PARAM NAME="gameover" VALUE="sound/gameover.wav"><!-- ゲームオーバーの時にならす音 -->
<PARAM NAME="kiri" VALUE="sound/kiri.wav"><!-- 自機が切り取り移動するときの音 -->
<PARAM NAME="butukari" VALUE="sound/butukari.wav"><!-- 敵とぶつかって死ぬときの爆発音 -->
<PARAM NAME="kakomi" VALUE="sound/kakomi.wav"><!-- 自領域を囲んだ時の成功音 -->
<!-- ゲームコンフィグ設定 -->
<PARAM NAME="parsent" VALUE="75"><!-- 何%以上切るとクリアーにするか[1～100] -->
<PARAM NAME="teki_kazu" VALUE="7"><!-- 敵の数[0～7] -->
<PARAM NAME="teki0_speed" VALUE="4"><!-- 敵1の速さ[1～10] -->
<PARAM NAME="teki1_speed" VALUE="4"><!-- 敵2の速さ[1～10] -->
<PARAM NAME="teki2_speed" VALUE="2"><!-- 敵3の速さ[1～10] -->
<PARAM NAME="teki3_speed" VALUE="3"><!-- 敵4の速さ[1～10] -->
<PARAM NAME="teki4_speed" VALUE="2"><!-- 敵5の速さ[1～10] -->
<PARAM NAME="teki5_speed" VALUE="2"><!-- 敵6の速さ[1～10] -->
<PARAM NAME="teki6_speed" VALUE="1"><!-- 敵7の速さ[1～10] -->
<PARAM NAME="time" VALUE="1000"><!-- 時間制限[0～1000(秒)] -->
<PARAM NAME="kisuu" VALUE="1"><!-- 自機ライフポイント[1～99] -->
<PARAM NAME="stage_clear_massage" VALUE="ステージクリアー！！"><!-- ステージクリアー時のメッセージ -->
<PARAM NAME="gameover_massage" VALUE="GAME OVER"><!-- ゲームオーバー時のメッセージ -->
<PARAM NAME="font" VALUE="24"><!-- パラメータ表示、メッセージのフォントサイズ[18～72] -->
<PARAM NAME="color" VALUE="#FFFF80"><!-- パラメータ表示、メッセージのフォントカラー[#000000～#FFFFFF] -->

</APPLET>

これらの VALUE=" " の値を変えて下さい。　間違えてNAME=" "は変えちゃ駄目です。
NAME=" "は変えてしまうとゲームが正常に動きません。変えるのはVALUEです。
大体見たまんまなので　どれをいじるとどういった効果が現れるかはわかると思います。
しかし、このようにindex.html内にそのままこれらの設定情報を書き込んでしまうと
ゲームクリアー時に見られる完成画像をそのまま　index.html内の
<PARAM NAME="back" VALUE="CG/back.jpg"><!-- ステージ背景画像(切ったときに見える画像) -->
ここを見られてしまうと　ばれてしまいます（汗

そこで画像の場所、ファイル名がばれてしまうのが嫌だ。という人は
これらの記述を省いて下さい。
ぶっちゃけ、全部の記述を省いて

<APPLET CODE="quicks.class" archive="quicks.jar" width=640 height=480>
</APPLET>

だけでも　動きます。
このとき　記述しなかった部分のファイル名はすべてデフォルト名を使用することになっています。
デフォルト名は以下の通りです。

CG/player1.gif 自機の画像
CG/player1_kiri.gif 自機切り取り中の画像
CG/teki0.gif 敵1の画像
CG/teki1.gif 敵2の画像
CG/teki2.gif 敵3の画像
CG/teki3.gif 敵4の画像
CG/teki4.gif 敵5の画像
CG/teki5.gif 敵6の画像
CG/teki6.gif 敵7の画像
CG/front.jpg ステージ前面画像(切る前の画像)
CG/back.jpg ステージ背景画像(切ったときに見える画像)
no クリアーした後に自動的に飛ぶページのURL、"no"と書いておくと飛ばない

sound/start.wav ゲーム開始の時にならす音
sound/stageclear.wav ゲームクリアーの時にならす音
sound/gameover.wav ゲームオーバーの時にならす音
sound/kiri.wav 自機が切り取り移動するときの音
sound/butukari.wav 敵とぶつかって死ぬときの爆発音
sound/kakomi.wav 自領域を囲んだ時の成功音

何%以上切るとクリアーにするか[1～100] 85%
敵の数 7
敵1～7までの動くスピード 2
時間制限[0～1000(秒)] 1000秒
自機ライフポイント[1～99] 5
クリアー時のメッセージ 「ステージクリアー！！ 」
ゲームオーバー時のメッセージ 「GAME OVER」
パラメータ表示、メッセージのフォントサイズ[18～72] 24
パラメータ表示　メッセージのフォントカラー[#0000～#FFFFFF] #FFFFFF(白)

自分で設定したい項目だけ記述してもいいですね。
お好みに合わせて設定してください。

クリアーした時に飛ぶURLについての詳細説明
・nextステージへいけるようになります。
　(クリアーすると好きなURLに飛べるようになってます。)
　やり方は簡単で
<PARAM NAME="gameclear_URL" VALUE="stage2.html">などと書くだけです。
アドレスの記述は相対パスでも絶対パスでも大丈夫です。
<PARAM NAME="gameclear_URL" VALUE="no">のように「no」と書いておくとこの機能は停止し
マウスクリックすると画面が全部見えてお終いになります。
記述があるときは　ステージクリアーして、マウスをクリックするとそのURLへ自動的に飛びます。
また、この記述自体をしない場合デフォルト設定でのアドレス
「./next_stage/index.html」へ飛ぶように作りました。
HTMLを盗み見られてしまうのが嫌な場合はこのデフォルトのアドレスを利用して下さい。

※BGMのbgm.wav、quicks.midですが、JAVAアプレットプログラム内でこれを再生することは推奨されていません。
　BGMを鳴らしたいときはホームページのHTMLファイル「index.html」内に音楽を再生するように
　タグを書くのが安全かつ楽でいいです。
例：<bgsound src="sound/bgm.wav" loop=-1>　　　←WAV形式のBGMを再生する場合
　　<bgsound src="sound/quicks.mid" loop=-1>　 ←MIDI形式のBGMを再生する場合
　　<bgsound src="sound/bgm.mp3" loop=-1>　　　←MP3形式のBGMなら容量も少なくて便利ですね♪
ちなみに　loop=-1　となっているのは無限ループ再生の設定です。

このゲームは結構重いです(汗
最低　Pentium2 300MHz
出来ればPentium3 500Mhzくらいは欲しいです。

このプログラムはフリーソフトです。
このプログラム・アプレットを使用して生じたいかなるトラブルについても、
作者は一切責任を負いません。

Java およびそのほか Java を含む標章は、米国 Sun Microsystems, Inc.の商標です。
Windowsは米国マイクロソフト社の商標です。

まだまだ開発途中の部分もありますれば、バグなどあると思いますが
皆様の暖かいご支援の元頑張っていく所存で御座います。
********************************************************************************
池田プロダクション
ホームページURL：http://ikeda.gotdns.com/
メールアドレス：ikeda_pro@yahoo.co.jp



おまけ
-------------------------------------------------------------------
以下は開発者・ホームページでの作品公開をする方への情報です。

プログラム開発者に向けて
■コンパイル方法について
JDK5.0がリリースされましたが、JDK5.0でコンパイルする際にソースファイルに若干の書き直しが必要になります。
これを回避する方法としてコンパイル時に次のオプションを指定することでバージョンを指定してのコンパイルが可能です。
>javac -source 1.3 quicks.java
-source 1.3とつけることで　JDK1.3バージョンでのソースファイルとして文法チェックをしてくれます。
これでJDK5.0を使っていても古い過去のソースファイルもコンパイル可能です。

またホームページなどで公開する際にユーザーの方々が皆JavaRuntime5.0をインストールしているとは限りません。
そういう場合JDK5.0でコンパイルしたファイルを読み込ませようと思ってもエラーになります。
そこでさきほどの-sourceに加え次のオプションも指定しましょう。
>javac -source 1.3 -target 1.3 *.java
これでソースファイルをJDK1.3のルールに基づいて文法チェックを行いコンパイルして吐き出すファイルも
JavaRuntime1.3対応のものになります。



ホームページでの作品公開にあたって
■IE互換ブラウザ　MSJVMで起こる問題
かつてMicrosoftが出していた非純正JavaRuntime　「MicroSoftJVM」が動いていると
今のJavaAppletは正常に動作しないことがあります。
これを解決するためにはMicroSoftJVMを切ってしまってSunの純正JVMをダウンロードしてきて
インストールしておけばいいのですが、純正IE以外の　いわゆる互換ブラウザーと呼ばれるブラウザだと
インターネットオプションでMicroSoftJVMを切っても　互換ブラウザー内部ではMicroSoftJVMを使ってしまう場合があります。
これを回避する手段として次の方法があります。

1.スタートボタンをクリックし、「ファイル名を指定して実行」をクリックします。 
2.「ファイル名を指定して実行」ウィンドウが表示されるので、「名前」に regsvr32 /u msjava.dll と入力して「OK」をクリックします。 
3.「RegSvr32」ダイアログに「DllUnregisterServer in msjava.dll succeeded.」と表示されるので、「OK」ボタンをクリックして閉じます。この通り表示されない場合は入力間違いがないか確認して下さい。 
4.ツール＞インターネットオプションをクリックします。 
5.「インターネットオプション」ウィンドウが表示されるので、「詳細設定」タブをクリックします。 
6.「Java (Sun)」のカテゴリにある「<applet> に Java 2 v1.x.x を使用（再起動が必要）」をクリックしてチェックを入れます。 
7.互換ブラウザ（Sleipnirとか） を一度終了し、再起動すれば設定完了です。 

なお、regsvr32 /u msjava.dll を実行すると Microsoft 社製のプラグインは使われなくなりますが、
再度使えるようにしたい場合は regsvr32 msjava.dll を実行します。

ホームページで公開しても　ユーザーの方々から「画面が灰色です。」「動きません」といった意見がきたら
参考にして下さい。
基本的に対処すべき問題は次のような項目があります。

・出来れば純正IEを使ってもらう
・Sun純正のJVMをダウンロードしてインストールしてもらう
・インターネットオプションでMicroSoftJVMは使わずにSun純正JVMを使う設定にする
・互換ブラウザーを使うなら　上で挙げた対処法を試す


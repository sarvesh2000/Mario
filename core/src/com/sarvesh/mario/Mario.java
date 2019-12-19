package com.sarvesh.mario;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class Mario extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int manState=0;
	int pause=0;
	float gravity=0.2f;
	float velocity=0;
	int manY=0;
	Random random;
	Rectangle manRectangle;
	int score=0;
	int gameState=0;
	Texture dizzy;
	Music bgmusic;
	Music gameover;
	Music coins;
	Music bombs;
	Music jump;

	ArrayList<Integer> coinXs = new ArrayList<Integer>();
	ArrayList<Integer> coinYs = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangles= new ArrayList<Rectangle>();
	Texture coin;
	int coinCount;

	ArrayList<Integer> bombXs = new ArrayList<Integer>();
	ArrayList<Integer> bombYs = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangles= new ArrayList<Rectangle>();
	Texture bomb;
	int bombCount;
	BitmapFont font;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man= new Texture[4];
		man[0]= new Texture("frame-1.png");
		man[1]= new Texture("frame-2.png");
		man[2]= new Texture("frame-3.png");
		man[3]= new Texture("frame-4.png");
		manY=Gdx.graphics.getHeight()/2;
		coin= new Texture("coin.png");
		random= new Random();
		bomb= new Texture("bomb.png");
		font= new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		dizzy= new Texture("dizzy-1.png");
		bgmusic= Gdx.audio.newMusic(Gdx.files.internal("Mario.mp3"));
		bgmusic.setLooping(true);
		bgmusic.setVolume(1);
		gameover=Gdx.audio.newMusic(Gdx.files.internal("smb_gameover.wav"));
		gameover.setVolume(1);
		gameover.setLooping(false);
		coins= Gdx.audio.newMusic(Gdx.files.internal("smb_coin.wav"));
		coins.setLooping(false);
		coins.setVolume(1);
		bombs= Gdx.audio.newMusic(Gdx.files.internal("smb_mariodie.wav"));
		bombs.setVolume(1);
		bombs.setLooping(false);
		jump= Gdx.audio.newMusic(Gdx.files.internal("smb_jump-small.wav"));

	}

	public void makeBomb(){
		float height= random.nextFloat()*Gdx.graphics.getHeight();
		bombYs.add((int)height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	public void makeCoin(){
		float height= random.nextFloat()*Gdx.graphics.getHeight();
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		int count=0;
		batch.begin();
		bgmusic.play();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		if(gameState==1){
			count=0;
			//GAME IS LIVE
			//BOMBS
			if(bombCount<250)
				bombCount++;
			else {
				bombCount = 0;
				makeBomb();
			}

			bombRectangles.clear();
			for (int i=0;i<bombXs.size();i++){
				batch.draw(bomb,bombXs.get(i),bombYs.get(i));
				bombXs.set(i,bombXs.get(i)-8);
				bombRectangles.add(new Rectangle(bombXs.get(i),bombYs.get(i),bomb.getWidth(),bomb.getHeight()));
			}

			//COINS
			if(coinCount<100)
				coinCount++;
			else {
				coinCount = 0;
				makeCoin();
			}

			coinRectangles.clear();
			for (int i=0;i<coinXs.size();i++){
				batch.draw(coin,coinXs.get(i),coinYs.get(i));
				coinXs.set(i,coinXs.get(i)-4);
				coinRectangles.add(new Rectangle(coinXs.get(i),coinYs.get(i),coin.getWidth(),coin.getHeight()));
			}

			if(Gdx.input.justTouched()) {
				velocity = -10;
				jump.play();
			}
			if(pause<8)
				pause++;
			else {
				pause = 0;
				if (manState < 3)
					manState++;
				else
					manState = 0;
			}
			velocity+=+gravity;
			manY-= velocity;
			if(manY<=0)
				manY=0;

		}
		else if(gameState==0){
			//GAME WAITING TO START
			if(Gdx.input.justTouched())
				gameState=1;
		}
		else if(gameState==2){
			bombs.play();
			//GAME OVER
			if(Gdx.input.justTouched()){
				gameState=1;
				manY=Gdx.graphics.getHeight()/2;
				score=0;
				velocity=0;
				coinXs.clear();
				coinYs.clear();
				coinRectangles.clear();
				coinCount=0;
				bombXs.clear();
				bombYs.clear();
				bombRectangles.clear();
				bombCount=0;

			}
		}

		if(gameState==2){
			batch.draw(dizzy,Gdx.graphics.getWidth()/2 - man[0].getWidth()/2,manY);
			Gdx.app.log("state","2");
		}else
			batch.draw(man[manState],Gdx.graphics.getWidth()/2 - man[0].getWidth()/2,manY);
		manRectangle= new Rectangle(Gdx.graphics.getWidth()/2 - man[0].getWidth()/2,manY,man[manState].getWidth(),man[manState].getHeight());
		for (int i=0;i<coinRectangles.size();i++){
			if(Intersector.overlaps(manRectangle,coinRectangles.get(i))) {
				coins.play();
				score++;
				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}
		for (int i=0;i<bombRectangles.size();i++){
			if(Intersector.overlaps(manRectangle,bombRectangles.get(i))) {
				Gdx.app.log("Bomb!", "Collision");
				bgmusic.stop();
				gameState = 2;
			}
		}

		font.draw(batch,String.valueOf(score),100,200);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		bgmusic.dispose();
		coins.dispose();
		gameover.dispose();
		bombs.dispose();
		jump.dispose();
	}
}

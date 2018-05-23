package com.k_means;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.k_means.Pixel;

/**
 * k_means算法 1、从D中随机取k个元素，作为k个簇的各自的中心。 2、分别计算剩下的元素到k个簇中心的相异度，将这些元素分别划归到相异度最低的簇。
 * 3、根据聚类结果，重新计算k个簇各自的中心，计算方法是取簇中所有元素各自维度的算术平均数。 4、将D中全部元素按照新的中心重新聚类。
 * 5、重复第4步，直到聚类结果不再变化。 6、将结果输出。
 * 
 * @author 阿浮
 *
 */
public class K_meansAlgorithm {
	// 中心点集合
	private Pixel[] centers;
	// 像素点集合
	private Pixel[][] pixel_list;
	// 每个簇各个方向上数据的总和
	private Pixel[] centerSum;

	// 生成随机k个中心点
	private Pixel[] setCenterPoint(int k) {
		centers = new Pixel[k];
		centerSum = new Pixel[k];
		int x;
		int y;
		for (int i = 0; i < k; i++) {
			Pixel center = new Pixel();
			Pixel center_sum = new Pixel();
			x = (int) (Math.random() * pixel_list.length);
			y = (int) (Math.random() * pixel_list[0].length);
			center.r = (double)pixel_list[x][y].r;
			center.g = (double)pixel_list[x][y].g;
			center.b = (double)pixel_list[x][y].b;
			center.group = i;
			centers[i] = center;
			center_sum.r = center.r;
			center_sum.g = center.g;
			center_sum.b = center.b;
			// group用于纪录sum中的元素的个数
			center_sum.group = 0;
			centerSum[i] = center_sum;
			x = 0;
			y = 0;
		}
		System.out.println("输出" + k + "初始中心");
		for (int i = 0; i < centers.length; i++) {
			System.out
					.println("(" + centers[i].r +","+ centers[i].g+"," + centers[i].g+"," + centers[i].b+"," + centers[i].group + ")");
		}
		return centers;
	}

	// 将各个点分类
	private void clusterSet() {
		List<Double> distance = new ArrayList<Double>();
		List<Pixel> pixel_group = new ArrayList<Pixel>();
		for (int i = 0; i < pixel_list.length; i++) {
			for (int j = 0; j < pixel_list[0].length; j++) {
				// 为每个点进行分类
				for (int p = 0; p < centers.length; p++) {
					// 将中心点的距离与像素点的距离放到list中
					distance.add(distanceCalculate(centers[p], pixel_list[i][j]));
					// 将所有的中心点翻入list
					pixel_group.add(centers[p]);
				}
				int min_pixel = minDiatance(distance);
				// 为像素点分类
				pixel_list[i][j].group = pixel_group.get(min_pixel).group;
				centerSum[min_pixel].r += pixel_list[i][j].r;
				centerSum[min_pixel].g += pixel_list[i][j].g;
				centerSum[min_pixel].b += pixel_list[i][j].b;
				centerSum[min_pixel].group += 1;
				// 将list清空
				distance.clear();
				pixel_group.clear();
			}
		}
	}

	/*
	 * 计算两点的欧式距离，rgb作为三维坐标。 计算距离即分类操作，计算两点的相似处来进行分类，因此要用各个点的属性进行计算
	 */
	private Double distanceCalculate(Pixel center, Pixel pixel) {
		double distance = 0;
		distance = Math.sqrt(Math.pow((center.r - pixel.r), 2) + Math.pow((center.g - pixel.g), 2)
				+ Math.pow((center.b - pixel.b), 2));
		return distance;
	}

	// 最小距离
	private int minDiatance(List<Double> distance) {
		double min_distance = distance.get(0);
		int min_pixel = 0;
		// 取出list中最小的距离
		for (int i = 0; i < distance.size(); i++) {
			if (distance.get(i) < min_distance) {
				min_distance = distance.get(i);
				min_pixel = i;
			}
			if (distance.get(i) == min_distance) {
				if (Math.random() * 10 < 5) {
					min_pixel = i;
				}
			}
		}
		return min_pixel;
	}

	// 确定各个簇的新的中心点
	private void setNewCenter() {
		for (int i = 0; i < centers.length; i++) {
			// 更新中心点
			centers[i].r =(int) (centerSum[i].r / centerSum[i].group);
			centers[i].g = (int)(centerSum[i].g / centerSum[i].group);
			centers[i].b =(int) (centerSum[i].b / centerSum[i].group);
			System.out.println(
					i + ":" + centerSum[i].group + ":" + centerSum[i].r + ":" + centerSum[i].g + ":" + centerSum[i].b);
			// 重置之前的求和结果
			centerSum[i].r = centers[i].r;
			centerSum[i].g = centers[i].g;
			centerSum[i].b = centers[i].b;
			centerSum[i].group = 0;
		}
	}

	// 读取图片
	public int[][] readImage(String path) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("读取图片失败");
			e.printStackTrace();
		}
		int width = image.getWidth();
		int height = image.getHeight();
		int[][] image_data = new int[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				image_data[i][j] = image.getRGB(i, j);
			}
		}
		return image_data;
	}

	// 用来获取图像元素，放到我们要做聚类的数组中
	private Pixel[][] initPixel(int[][] data) {
		Pixel[][] image_pixel = new Pixel[data.length][data[0].length];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				Pixel pixel_image  = new Pixel();;
				Color color = new Color(data[i][j]);
				pixel_image.r = color.getRed();
				pixel_image.g = color.getGreen();
				pixel_image.b = color.getBlue();
				pixel_image.group=1;
				image_pixel[i][j]=pixel_image;
			}
		}
		return image_pixel;
	}

	// 输出图片
	private void writeImage(String path) {
		Color co1 = new Color(255, 0, 0);
		Color co2 = new Color(0, 255, 0);
		Color co3 = new Color(0, 0, 255);
		Color co4 = new Color(128,128,128);
		BufferedImage write_image = new BufferedImage(pixel_list.length, pixel_list[0].length,
				BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < pixel_list.length; i++) {
			for (int j = 0; j < pixel_list[0].length; j++) {
				if (pixel_list[i][j].group == 0)
					write_image.setRGB(i, j, co1.getRGB());
				else if (pixel_list[i][j].group == 1)
					write_image.setRGB(i, j, co2.getRGB());
				else if (pixel_list[i][j].group == 2)
					write_image.setRGB(i, j, co3.getRGB());
				else if (pixel_list[i][j].group == 3)
					write_image.setRGB(i, j, co4.getRGB());
			}
		}
		try {
			ImageIO.write(write_image, "jpg", new File(path));
		} catch (IOException e) {
			System.out.println("输出图片出错");
			e.printStackTrace();
		}
	}
	
//	进行基于K-均值聚类的彩色图像分割的总方法
	public void kmeans(String path,int k,int m){
		pixel_list=initPixel(readImage(path));
		setCenterPoint(k);
		for(int level=0;level<m;level++){
			clusterSet();
			setNewCenter();
			for (int i = 0; i < centers.length; i++)
			{				
				System.out.println("("+centers[i].r+","+centers[i].g+","+centers[i].b+")");
			}
		}
		clusterSet();
		System.out.println("第"+m+"次迭代完成，聚类中心为：");
		for (int i = 0; i < centers.length; i++)
		{				
			System.out.println("("+centers[i].r+","+centers[i].g+","+centers[i].b+")");
		}
		System.out.println("迭代总次数："+m);//进行图像输出，这个随意改
		writeImage("F:\\k_mean\\aftergril.jpg");
		System.out.println("分割完成");
	}

	
}

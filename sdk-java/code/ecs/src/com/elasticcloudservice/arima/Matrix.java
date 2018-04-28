package com.elasticcloudservice.arima;


/**
 * @Author: Skye
 * @Date: 9:42 2018/4/24
 * @Description:
 */
public class Matrix {
    private int r_num;
    private int c_num;
    private double[][] content;

    public Matrix(int r, int c) {
        this.r_num = r;
        this.c_num = c;
        this.content = new double[this.r_num][this.c_num];
        for (int i = 0; i < r_num; i++)
            for (int j = 0; j < c_num; j++) {
                this.content[i][j] = 0;
            }
    }

    public Matrix(double[] vals, int m) {
        this.r_num = m;
        this.c_num = m != 0 ? vals.length / m : 0;
        if (m * this.c_num != vals.length) {
            throw new IllegalArgumentException("Array length must be a multiple of m.");
        } else {
            this.content = new double[m][this.c_num];

            for(int i = 0; i < m; ++i) {
                for(int j = 0; j < this.c_num; ++j) {
                    this.content[i][j] = vals[i + j * m];
                }
            }
        }
    }

    public Matrix(double[][] in) {
        this.r_num = in.length;
        this.c_num = in[0].length;
        this.content = new double[this.r_num][this.c_num];
        for (int i = 0; i < r_num; i++)
            for (int j = 0; j < c_num; j++) {
                this.content[i][j] = in[i][j];
            }
    }

    public Matrix(int r, int c, int module) {
        this.r_num = r;
        this.c_num = c;
        this.content = new double[this.r_num][this.c_num];
        for (int i = 0; i < this.r_num; i++)
            for (int j = 0; j < this.c_num; j++) {
                this.content[i][j] = (int) (Math.random() * module);
            }
    }

    public int getRowDimension() {
        return r_num;
    }

    public int getColDimension() {
        return c_num;
    }

    public double get(int i, int j) {
        return this.content[i][j];
    }

    public double[][] getContent() {
        return content;
    }

    public void setContent(double[][] content) {
        this.content = content;
    }

    public Matrix add(Matrix A) {
        if (this.r_num != A.r_num || this.c_num != A.c_num) {
            return null;
        }
        Matrix B = new Matrix(A.r_num, A.c_num);
        for (int i = 0; i < r_num; i++) {
            for (int j = 0; j < c_num; j++) {
                B.content[i][j] = this.content[i][j] + A.content[i][j];
            }
        }
        return B;
    }

    public Matrix add(Matrix A, double module) {
        if (this.r_num != A.r_num || this.c_num != A.c_num) {
            return null;
        }
        Matrix B = new Matrix(A.r_num, A.c_num);
        for (int i = 0; i < r_num; i++) {
            for (int j = 0; j < c_num; j++) {
                B.content[i][j] = (this.content[i][j] + A.content[i][j]) % module;
            }
        }
        return B;
    }

    // compute this-A
    public Matrix sub(Matrix A) {
        if (this.r_num != A.r_num || this.c_num != A.c_num) {
            return null;
        }
        Matrix B = new Matrix(A.r_num, A.c_num);
        for (int i = 0; i < r_num; i++) {
            for (int j = 0; j < c_num; j++) {
                B.content[i][j] = this.content[i][j] - A.content[i][j];
            }
        }
        return B;
    }

    public Matrix sub(Matrix A, double module) {
        if (this.r_num != A.r_num || this.c_num != A.c_num) {
            return null;
        }
        Matrix B = new Matrix(A.r_num, A.c_num);
        for (int i = 0; i < r_num; i++) {
            for (int j = 0; j < c_num; j++) {
                double tmp = this.content[i][j] - A.content[i][j];
                if (tmp >= 0)
                    B.content[i][j] = tmp % module;
                if (tmp < 0)
                    B.content[i][j] = tmp % module + module;
            }
        }
        return B;
    }

    // compute this*A
    public Matrix multiply(Matrix A) {
        if (this.c_num != A.r_num) {
            return null;
        }
        Matrix B = new Matrix(this.r_num, A.c_num);
        for (int i = 0; i < this.r_num; i++) // the row of the this
        {
            for (int j = 0; j < A.c_num; j++) // the column of the A
            {
                double temp = 0;
                for (int k = 0; k < A.r_num; k++) // the row of A
                {
                    temp += this.content[i][k] * A.content[k][j];

                }
                B.content[i][j] = temp;

            }
        }
        return B;
    }




    public Matrix multiply(Matrix A, int module) {
        if (this.c_num != A.r_num) {
            return null;
        }
        Matrix B = new Matrix(this.r_num, A.c_num);
        for (int i = 0; i < this.r_num; i++) // the row of the this
        {
            for (int j = 0; j < A.c_num; j++) // the column of the A
            {
                double temp = 0;
                for (int k = 0; k < A.r_num; k++) // the row of A
                {
                    temp += this.content[i][k] * A.content[k][j]%module;

                }
                B.content[i][j] = temp;

            }
        }
        return B;
    }

    public double Matrix2Det(){
        double sum=this.content[0][0]*this.content[1][1]-this.content[0][1]*this.content[1][0];
        return sum;
    }


    public void CompanionMatrix(Matrix in, int r, int c){
        this.r_num=in.r_num-1;
        this.c_num=in.c_num-1;
        int k=0;
        for(int i=0;i<in.r_num;i++)
        {
            int z=0;
            if(i==r)
                i++;
            if(i==in.r_num)
                break;
            for(int j=0;j<in.c_num;j++)
            {

                if(j==c)
                    j++;
                if(j==in.c_num)
                    break;
                this.content[k][z]=in.content[i][j];
                z++;
            }
            k++;
        }
    }

    private int IndexOfNe1(int n){
        int sum=1;
        for(int i=0;i<n;i++)
            sum*=-1;
        return sum;
    }

    //comput the determinant of a matrix
    public double MatrixDet(){
        if(this.c_num!=this.r_num)
            return 0;
        else{
            int num=this.c_num;
            double sum=0;
            if(this.c_num==2){
                return this.Matrix2Det();
            }
            if(this.c_num>3){
                for(int i=0;i<num;i++){
                    Matrix tmp=new Matrix(num-1,num-1);
                    tmp.CompanionMatrix(this, 0, i);
                    sum+=this.content[0][i]*tmp.MatrixDet()*IndexOfNe1(i);
                }
                return sum;
            }
            else{
                return this.Matrix3Det();
            }

        }
    }


    //comput the determinant of a 3-matrix
    public double Matrix3Det(){
        if(this.r_num!=this.c_num)
            return 0;
        int num=this.r_num;
        double[] re_tmp=new double[this.c_num];
        double sum1=0;
        double sum2=0;
        for(int k=0;k<num;k++){
            re_tmp[k]=1;
            int i=0;
            int j=0;
            for(int z=0;z<num;z++){

                if(k+z<num){
                    re_tmp[k]*=this.content[z][k+z];
                }
                else{
                    re_tmp[k]*=this.content[num-i-1][k-j-1];
                    i++;
                    j++;
                }
            }
            sum1+=re_tmp[k];

        }
        for(int k=num-1;k>=0;k--){
            re_tmp[k]=1;
            int i=0;
            int j=0;
            for(int z=0;z<num;z++){

                if(k-z>=0){
                    re_tmp[k]*=this.content[z][k-z];
                }
                else{
                    re_tmp[k]*=this.content[num-i-1][k+j+1];
                    i++;
                    j++;
                }
            }
            sum2+=re_tmp[k];
        }
        return sum1-sum2;
    }

    public Matrix MatrixInverse(){

        double det=this.MatrixDet();
        if(det==0)
            return null;
        Matrix re=new Matrix(this.r_num,this.c_num);
        for(int i=0;i<this.r_num;i++){
            for(int j=0;j<this.c_num;j++){
                Matrix tmp=new Matrix(this.r_num-1,this.c_num-1);
                tmp.CompanionMatrix(this, i, j);
                re.content[i][j]=tmp.MatrixDet()/det*IndexOfNe1(i+j);
            }
        }
        return re.Transpose();
    }

    public Matrix Transpose() {
        Matrix re = new Matrix(this.r_num, this.c_num);
        for (int i = 0; i < this.r_num; i++) {
            for (int j = 0; j < this.c_num; j++) {
                re.content[j][i] = this.content[i][j];
            }
        }
        return re;
    }

    public void printMatlab() {
        System.out.print("[");
        for (int i = 0; i < this.r_num; i++) {

            for (int j = 0; j < this.c_num; j++)
            {
                System.out.print(this.content[i][j]);
                if(j!=this.c_num-1)
                    System.out.print(" ");
            }

            if(i<this.r_num-1)
                System.out.print(";");
        }
        System.out.println("]");
    }

    public void print() {
        for (int i = 0; i < this.r_num; i++) {

            for (int j = 0; j < this.c_num; j++)
            {
                System.out.print(this.content[i][j]);
                System.out.print(" ");
            }
            System.out.println(" ");
        }
        System.out.println(" ");
    }



    public static void main(String[] args) {
        double[][] a = { { 10, 22 }, { 15, 23 } };
        double[][] a1 = { { 0, 1,2 }, { 1, 1,4 } ,{2,-1,0}};
        double[][] a2={{1,2,3},{2,2,1},{3,4,3}};
        double[][] a3={{1,1,1,1},{2,4,3,1},{4,16,9,1},{8,64,27,1}};
        double[][] a4={{-2,2,-4,0,5},{2,1,2,0,5},{4,3,1,2,7},{3,1,2,4,-5},{6,-1,2,7,-5}};
        Matrix A = new Matrix(a4);
        Matrix B = new Matrix(5,5);

        System.out.println(A.MatrixDet());
        A.print();
        B=A.MatrixInverse();
        B.print();
        //System.out.println("attention"+" "+A.MatrixDet());
        Matrix C=A.multiply(B);
  //      C.print();



    }
}
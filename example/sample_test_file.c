
float avg(int count, int value[4]){

float total;

total= 0.0;

for (i =0; i < count; i=i+1) {

   	total = total + value[i];

}

return(total/count);

}

 

int main(){

   int studentNumber, count, i, sum;

int mark[4];

float average;

 

count = 4;

      sum = 0;

for (i =0; i < count; i=i+1) {

mark[i] = i * 30;

sum = sum + mark[i];

 	average = avg(i+1, mark);

if (average > 40.0 ) {

printf("%f", average);

}

}

}
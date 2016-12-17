int i,j ;
int num;
int size;
int temp;
int ar[100];
int main(){
	scanf(size);
	j = size/2;
	num = 1;
	for( i = 0; num <= size*size; num = num + 1){
		ar[i* size +j] = num;
		temp = num / size;
		if(temp * size - num == 0)
		{
			i = i +1;
		}
		else{
			i = i -1;
			j = j +1;
		}
		if(i<0){
			i= size -1;
		}
		if(j>(size-1)){
			j= 0;
		}

	}
	for (i = 0 ; i<size; i = i +1)
	{
		for(j= 0 ; j< size; j = j + 1){
			printf(ar[i*size +j]);
		}
	}
}
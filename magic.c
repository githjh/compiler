int ar[size][size];

int main(){
	int i,j ;
	int num;
	int size;
	size = 3;
	for( i = 0, j = size/2, num = 1; num <= size*size; num++){
		ar[i][j] = num;
		if(num %size == 0){
			i++;
		}
		else{
			i--;
			j++;
		}
		if(i<0){
			i= size -1;
		}
		if(j>(size-1)){
			j= 0;
		}

	}
	for (i = 0 ; i<size; i++)
	{
		for(j= 0 ; j< size; j++){
			printf(ar[i][j]);
		}
	}
}
float var10;

int add(int a, int b){
	int c;
	return a+b;
}
int mul(float c, float d){
	return c * d;
}

int fact(int a){
	if (a == 1)
		return 1;
	return a * fact (a-1);
}

int main(int b)
{
	int var1;
	int arr[10];
	int i;
	arr[5] = fact(5);
	printf(arr[5]);
	var10 = 5;
	switch(var10){
		case 1:
			var10 = 1;
			printf(1);
		case 2:
			var10 =2;
			printf(2);
		case 5:
			var10 = 5;
			printf(var10);
			break;
		default:
			var10 = 10;
			printf(var10);
			break;
	}
	i = 0;
	do{
		arr[i] = i;
		printf(arr[i]);
		i = i +1;
	}while(i < 10);
	for(i = 0; i < 10 ; i = i +1){
		printf(i);
	}

}
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

int multi(int a, int b){
	int c;
	int d;
	c = 1;
	d = 2;
	return add(a,c) + mul(b,d);
}

int main(int b)
{
	int var1;
	int arr[10];
	int i;
	arr[5] = fact(5);
	printf(arr[5]);
	var10 = 5;
	var1 = var10 * 1;
	printf(var1);
	var1 = 2 * 3.3;
	
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
	while( i < 20){
		int wh;
		wh = i + 100;
		printf(wh);
		i  = i +1;
	}
	printf(multi(2,3));
	printf(i);
	if ( i <= 20){
		printf (1);
	}
	else{
		printf(0);
	}
	return 0;
}
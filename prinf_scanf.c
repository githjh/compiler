int fact(int a){
	if(a == 1)
		return 1;
	else{
		return  a+ fact(a-1);
	}
}
int fib(int a){
	if (a == 1){
		return 1;
	}
	if (a == 2){
		return 2;
	}
	return fib(a-2)+fib(a-1);
}

int main(int b)
{
	int a;
	a = 10;
	printf(fib(a)+fact(a));
}
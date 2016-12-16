int fact(int a){
	if(a == 1)
		return 1;
	else{
		return a + fact(a-1);
	}
}

int main(int b)
{
	int a;
	a = 10;
	printf(fact(a));
}
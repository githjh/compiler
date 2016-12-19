int main(int a){
	int b;
	{
		int c;
		int d;
		b = 2;
		c = 3;
		d = b *c;
		printf(d);
		for(c = 0 ; c< 10; c = c +1){
			int v1;
			int v2;
			int v3[10];
			int v4;
			v3[c] = c;
			printf(v3[c]);
		}
		printf(b);
		printf(c);
		printf(d);

	}
	{
		int c;
		int d;
		b = 3;
		c = 2;
		d = b *c;
		printf(d);
	}
	{
		int c[10];
		int i;
		b = 10;
		for(i = 0; i< 10; i = i +1){
			int tmp;
			c[i] = i;
			tmp = c[i] * b;
			printf(tmp);
		}
	}
	return 0;
}
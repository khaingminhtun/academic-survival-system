interface CardProps {
  children: React.ReactNode;
  className?: string;
  padding?: 'sm' | 'md' | 'lg' | 'none';
}

export default function Card({ children, className = '', padding = 'md' }: CardProps) {
  const paddingMap = {
    none: '',
    sm: 'p-4',
    md: 'p-5',
    lg: 'p-6',
  };

  return (
    <div className={`rounded-xl bg-white border border-slate-200 shadow-sm ${paddingMap[padding]} ${className}`}>
      {children}
    </div>
  );
}

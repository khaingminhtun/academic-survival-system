interface TopBarProps {
  onMenuClick: () => void;
}

export default function TopBar({ onMenuClick }: TopBarProps) {
  return (
    <header className="flex h-16 items-center gap-4 border-b border-slate-200 bg-white px-4 sm:px-6 lg:px-8 flex-shrink-0">
      {/* Mobile hamburger */}
      <button
        onClick={onMenuClick}
        className="p-2 -ml-2 rounded-md text-slate-500 hover:text-slate-700 hover:bg-slate-100 lg:hidden"
        aria-label="Open navigation"
      >
        <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
          <path strokeLinecap="round" strokeLinejoin="round" d="M4 6h16M4 12h16M4 18h16" />
        </svg>
      </button>

      <div className="flex-1" />

      {/* Status indicator */}
      <div className="flex items-center gap-2 text-xs text-slate-500">
        <span className="inline-block w-2 h-2 rounded-full bg-green-400" />
        <span className="hidden sm:inline">User Active</span>
      </div>
    </header>
  );
}

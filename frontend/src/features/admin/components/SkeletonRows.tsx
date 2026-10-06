const SkeletonRows: React.FC = () => (
  <div className="divide-y divide-gray-50">
    {[...Array(5)].map((_, i) => (
      <div key={i} className="px-5 py-4 flex items-center gap-4">
        <div className="w-8 h-8 rounded-full bg-gray-100 animate-pulse shrink-0" />
        <div className="flex-1 space-y-2">
          <div className="h-3.5 bg-gray-100 animate-pulse rounded w-1/3" />
          <div className="h-3 bg-gray-100 animate-pulse rounded w-1/2" />
        </div>
        <div className="h-5 w-14 bg-gray-100 animate-pulse rounded-full" />
      </div>
    ))}
  </div>
);

export default SkeletonRows;
